import java.util.ArrayList;

public class Game {

    private ArrayList<Player> players;
    private Deck deck;

    private int currentPlayer;

    public Game(int numberOfPlayers) {

        players = new ArrayList<>();
        deck = new Deck();

        for (int i = 1; i <= numberOfPlayers; i++) {
            players.add(new Player("Player " + i));
        }

        currentPlayer = 0;
    }

    public void startGame() {

        deck = new Deck();
        currentPlayer = 0;

        for (Player player : players) {

            player.clearCards();

            player.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void drawCard() {

        Player player = getCurrentPlayer();

        // Maximum of 3 cards
        if (player.getCards().size() < 3) {
            player.addCard(deck.drawCard());
        }
    }

    public boolean nextPlayer() {

        currentPlayer++;

        if (currentPlayer >= players.size()) {
            return false;
        }

        return true;
    }

    public boolean isLastPlayer() {
        return currentPlayer == players.size() - 1;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    public Player getWinner() {

        Player winner = players.get(0);

        for (Player player : players) {

            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }

        return winner;
    }

    public int getDeckSize() {
        return deck.size();
    }
}