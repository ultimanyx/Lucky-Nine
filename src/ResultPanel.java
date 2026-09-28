import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    private GameFrame frame;

    private JLabel winnerLabel;

    private JTextArea resultArea;

    public ResultPanel(GameFrame frame) {

        this.frame = frame;

        setLayout(new BorderLayout());

        setBackground(new Color(25, 25, 35));


        JLabel title =
            new JLabel(
                "GAME RESULT",
                SwingConstants.CENTER
            );

        title.setFont(
            new Font("Arial", Font.BOLD, 38)
        );

        title.setForeground(Color.WHITE);

        add(title, BorderLayout.NORTH);


        resultArea = new JTextArea();

        resultArea.setEditable(false);

        resultArea.setFont(
            new Font("Arial", Font.PLAIN, 22)
        );

        resultArea.setBackground(
            new Color(35, 35, 45)
        );

        resultArea.setForeground(Color.WHITE);

        resultArea.setMargin(
            new Insets(20, 20, 20, 20)
        );

        add(
            new JScrollPane(resultArea),
            BorderLayout.CENTER
        );


        winnerLabel = new JLabel(
            "",
            SwingConstants.CENTER
        );

        winnerLabel.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        winnerLabel.setForeground(Color.YELLOW);



        JButton newGame =
            new JButton("NEW GAME");

        newGame.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        newGame.addActionListener(e -> {
            frame.showStart();
        });

        JPanel bottom = new JPanel();

        bottom.setBackground(
            new Color(25, 25, 35)
        );

        bottom.add(newGame);

        bottom.add(winnerLabel);

        add(bottom, BorderLayout.SOUTH);
    }


    public void showResults(Game game) {

        StringBuilder result =
            new StringBuilder();

        result.append(
            "FINAL SCORES\n\n"
        );

        for (Player player : game.getPlayers()) {

            result.append(
                player.getName()
            );

            result.append(
                " : "
            );

            result.append(
                player.getScore()
            );

            result.append("\n");

            result.append(
                "Cards: "
            );

            for (Card card : player.getCards()) {

                result.append(card);

                result.append(" ");
            }

            result.append("\n\n");
        }

        resultArea.setText(
            result.toString()
        );

        Player winner =
            game.getWinner();

        winnerLabel.setText(
            "🏆 WINNER: "
            + winner.getName()
            + " - Score "
            + winner.getScore()
        );
    }
}