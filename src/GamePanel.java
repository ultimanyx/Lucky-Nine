import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private GameFrame frame;

    private Game game;

    private JLabel playerLabel;
    private JLabel cardLabel;
    private JLabel scoreLabel;
    private JLabel instructionLabel;

    private JButton drawButton;
    private JButton standButton;

    public GamePanel(GameFrame frame) {

        this.frame = frame;

        setLayout(new BorderLayout());

        setBackground(new Color(20, 100, 70));

        createUI();
    }

    private void createUI() {

        // TOP
        JPanel topPanel = new JPanel();

        topPanel.setBackground(new Color(15, 70, 50));

        playerLabel = new JLabel("Player");

        playerLabel.setForeground(Color.WHITE);

        playerLabel.setFont(
            new Font("Arial", Font.BOLD, 30)
        );

        topPanel.add(playerLabel);

        add(topPanel, BorderLayout.NORTH);


        // CENTER
        JPanel centerPanel = new JPanel();

        centerPanel.setOpaque(false);

        centerPanel.setLayout(
            new BoxLayout(
                centerPanel,
                BoxLayout.Y_AXIS
            )
        );

        cardLabel = new JLabel();

        cardLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        cardLabel.setFont(
            new Font("Arial", Font.BOLD, 40)
        );

        cardLabel.setForeground(Color.WHITE);

        scoreLabel = new JLabel("Score: ?");

        scoreLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        scoreLabel.setFont(
            new Font("Arial", Font.BOLD, 26)
        );

        scoreLabel.setForeground(Color.WHITE);

        instructionLabel =
            new JLabel(" ");

        instructionLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        instructionLabel.setForeground(Color.WHITE);

        instructionLabel.setFont(
            new Font("Arial", Font.PLAIN, 18)
        );

        centerPanel.add(Box.createVerticalGlue());

        centerPanel.add(cardLabel);

        centerPanel.add(Box.createVerticalStrut(20));

        centerPanel.add(scoreLabel);

        centerPanel.add(Box.createVerticalStrut(20));

        centerPanel.add(instructionLabel);

        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);


        // BOTTOM
        JPanel bottomPanel = new JPanel();

        bottomPanel.setOpaque(false);

        drawButton = new JButton("DRAW");

        standButton = new JButton("STAND");

        drawButton.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        standButton.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        drawButton.setPreferredSize(
            new Dimension(150, 50)
        );

        standButton.setPreferredSize(
            new Dimension(150, 50)
        );

        drawButton.addActionListener(e -> drawCard());

        standButton.addActionListener(e -> stand());

        bottomPanel.add(drawButton);

        bottomPanel.add(standButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    public void startNewGame(int numberOfPlayers) {

        game = new Game(numberOfPlayers);

        game.startGame();

        showCurrentPlayer();
    }


    private void showCurrentPlayer() {

        Player player =
            game.getCurrentPlayer();

        playerLabel.setText(
            player.getName() + "'s Turn"
        );

        cardLabel.setText(
            "🂠   🂠"
        );

        scoreLabel.setText(
            "Score: " + player.getScore()
        );

        instructionLabel.setText(
            "Draw another card or stand."
        );

        drawButton.setEnabled(
            player.getCards().size() < 3
        );

        standButton.setEnabled(true);
    }


    private void drawCard() {

        game.drawCard();

        Player player =
            game.getCurrentPlayer();

        displayCards(player);

        if (player.getCards().size() >= 3) {

            drawButton.setEnabled(false);

            instructionLabel.setText(
                "Maximum 3 cards. Press STAND."
            );
        }
    }


    private void stand() {

        if (game.isLastPlayer()) {

            frame.showResults();

        } else {

            showPassScreen();
        }
    }


    private void showPassScreen() {

        Player current =
            game.getCurrentPlayer();

        String message =
            current.getName()
            + " is finished.\n\n"
            + "Pass the PC to the next player.";

        JOptionPane.showMessageDialog(
            this,
            message,
            "Pass Device",
            JOptionPane.INFORMATION_MESSAGE
        );

        game.nextPlayer();

        showCurrentPlayer();
    }


    private void displayCards(Player player) {

        StringBuilder cards = new StringBuilder();

        for (Card card : player.getCards()) {

            cards.append(
                card.toString()
            );

            cards.append("   ");
        }

        cardLabel.setText(
            cards.toString()
        );

        scoreLabel.setText(
            "Score: " + player.getScore()
        );
    }


    public Game getGame() {
        return game;
    }
}