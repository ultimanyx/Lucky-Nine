import javax.swing.JFrame;
import java.awt.CardLayout;

public class GameFrame extends JFrame {

    private CardLayout cardLayout;

    private StartPanel startPanel;
    private GamePanel gamePanel;
    private ResultPanel resultPanel;

    public GameFrame() {

        setTitle("Lucky Nine");
        setSize(1000, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        cardLayout = new CardLayout();

        setLayout(cardLayout);

        startPanel = new StartPanel(this);
        gamePanel = new GamePanel(this);
        resultPanel = new ResultPanel(this);

        add(startPanel, "START");
        add(gamePanel, "GAME");
        add(resultPanel, "RESULT");

        cardLayout.show(getContentPane(), "START");

        setVisible(true);
    }

    public void startGame(int players) {

        gamePanel.startNewGame(players);

        cardLayout.show(getContentPane(), "GAME");
    }

    public void showResults() {

        resultPanel.showResults(
            gamePanel.getGame()
        );

        cardLayout.show(getContentPane(), "RESULT");
    }

    public void showStart() {

        cardLayout.show(getContentPane(), "START");
    }
}