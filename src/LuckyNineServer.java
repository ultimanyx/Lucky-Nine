import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LuckyNineServer {
    public static final int PORT = 5555;
    public static final int MAX_PLAYERS = 3;

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private int numPlayers = 0;

    // ---- Authoritative game state (all the stuff currently in Main) ----
    private final List<Main.Card> deck = new ArrayList<>();
    private final List<Main.Card> bankerHand = new ArrayList<>();
    private final List<List<Main.Card>> playerHands = new ArrayList<>();
    private final List<Boolean> playerHasDrawn = new ArrayList<>();
    private int activePlayerIndex = 0;
    private boolean roundFinished = true;
    private int roundNumber = 0;
    private String lastResult = "";

    public static void main(String[] args) throws IOException {
        new LuckyNineServer().start();
    }

    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server listening on port " + PORT);
        System.out.println("Your LAN IP: " + InetAddress.getLocalHost().getHostAddress());

        // Accept players
        while (clients.size() < MAX_PLAYERS) {
            Socket s = ss.accept();
            ClientHandler ch = new ClientHandler(s, clients.size());
            clients.add(ch);
            new Thread(ch).start();
            System.out.println("Player " + (clients.size()) + " joined: " + s.getRemoteSocketAddress());
        }

        numPlayers = clients.size();
        System.out.println("All players connected. Starting game.");

        // Init per-player state
        for (int i = 0; i < numPlayers; i++) {
            playerHands.add(new ArrayList<>());
            playerHasDrawn.add(false);
        }

        broadcast("START|" + numPlayers);
        startNewRound();
    }

    // ---- Game logic (moved/adapted from Main) ----

    synchronized void startNewRound() {
        roundNumber++;
        createDeck();
        bankerHand.clear();
        bankerHand.add(drawCard());
        bankerHand.add(drawCard());

        for (int i = 0; i < numPlayers; i++) {
            playerHands.get(i).clear();
            playerHands.get(i).add(drawCard());
            playerHands.get(i).add(drawCard());
            playerHasDrawn.set(i, false);
        }
        activePlayerIndex = 0;
        roundFinished = false;
        lastResult = "Round " + roundNumber + " — Player 1's turn";
        broadcastState();
    }

    synchronized void handleHit(int playerIndex) {
        if (roundFinished || playerIndex != activePlayerIndex) return;
        if (playerHasDrawn.get(playerIndex)) {
            clients.get(playerIndex).send("MSG|You already drew this round.");
            return;
        }
        playerHands.get(playerIndex).add(drawCard());
        playerHasDrawn.set(playerIndex, true);

        if (getScore(playerHands.get(playerIndex)) == 9) {
            lastResult = "Natural 9 for Player " + (playerIndex + 1) + "!";
            handleStand(playerIndex);
        } else {
            broadcastState();
        }
    }

    synchronized void handleStand(int playerIndex) {
        if (roundFinished || playerIndex != activePlayerIndex) return;
        activePlayerIndex++;

        if (activePlayerIndex < numPlayers) {
            lastResult = "Player " + (activePlayerIndex + 1) + "'s turn";
            broadcastState();
        } else {
            bankerPlay();
            roundFinished = true;
            determineWinners();
            broadcastState();
        }
    }

    private void bankerPlay() {
        if (getScore(bankerHand) <= 5 && bankerHand.size() < 3) {
            bankerHand.add(drawCard());
        }
    }

    private void determineWinners() {
        int bs = getScore(bankerHand);
        StringBuilder sb = new StringBuilder("RESULTS: ");
        for (int i = 0; i < numPlayers; i++) {
            int ps = getScore(playerHands.get(i));
            sb.append("P").append(i + 1).append("(").append(ps).append(") vs B(").append(bs).append("): ");
            if (ps > bs) sb.append("WIN ");
            else if (bs > ps) sb.append("LOSE ");
            else sb.append("TIE ");
            if (i < numPlayers - 1) sb.append("| ");
        }
        lastResult = sb.toString();
    }

    // ---- Protocol ----

    synchronized void broadcastState() {
        for (int i = 0; i < numPlayers; i++) {
            clients.get(i).send(buildStateFor(i));
        }
    }

    private String buildStateFor(int viewer) {
        StringBuilder sb = new StringBuilder("STATE|");
        sb.append(encodeHand(bankerHand)).append('|');
        for (int i = 0; i < numPlayers; i++) {
            // Hide other players' hands unless round is finished
            boolean reveal = roundFinished || i == viewer;
            sb.append(reveal ? encodeHand(playerHands.get(i)) : "HIDDEN").append('|');
        }
        // pad to 3 slots
        for (int i = numPlayers; i < 3; i++) sb.append("|");
        sb.append(activePlayerIndex).append('|');
        sb.append(roundFinished).append('|');
        sb.append(lastResult.replace("\n", " "));
        return sb.toString();
    }

    private String encodeHand(List<Main.Card> hand) {
        StringJoiner sj = new StringJoiner(",");
        for (Main.Card c : hand) sj.add(c.toString());
        return sj.toString();
    }

    private void broadcast(String line) {
        for (ClientHandler c : clients) c.send(line);
    }

    // ---- Deck helpers (copied from Main) ----
    private void createDeck() {
        deck.clear();
        String[] suits = {"♠", "♥", "♦", "♣"};
        String[] ranks = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        for (String s : suits) for (String r : ranks) deck.add(new Main.Card(r, s));
        Collections.shuffle(deck);
    }
    private Main.Card drawCard() {
        if (deck.isEmpty()) createDeck();
        return deck.remove(deck.size() - 1);
    }
    private int getScore(List<Main.Card> hand) {
        int t = 0; for (Main.Card c : hand) t += c.getValue(); return t % 10;
    }

    // ---- Per-client I/O thread ----
    class ClientHandler implements Runnable {
        final Socket socket;
        final int playerIndex;
        final PrintWriter out;
        final BufferedReader in;

        ClientHandler(Socket s, int idx) throws IOException {
            this.socket = s;
            this.playerIndex = idx;
            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);
            this.in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
        }

        void send(String line) { out.println(line); }

        @Override public void run() {
            try {
                send("WELCOME|" + playerIndex + "|" + MAX_PLAYERS);
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("HIT"))       handleHit(playerIndex);
                    else if (line.equals("STAND")) handleStand(playerIndex);
                    else if (line.equals("NEW_ROUND") && roundFinished) startNewRound();
                }
            } catch (IOException e) { /* ignore */ }
        }
    }
}