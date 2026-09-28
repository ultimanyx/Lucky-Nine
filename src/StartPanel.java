import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    private GameFrame frame;

    public StartPanel(GameFrame frame) {

        this.frame = frame;

        setLayout(new GridBagLayout());

        setBackground(new Color(25, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("LUCKY NINE");

        title.setFont(
            new Font("Arial", Font.BOLD, 42)
        );

        title.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;

        add(title, gbc);

        JLabel subtitle =
            new JLabel("Select Number of Players");

        subtitle.setFont(
            new Font("Arial", Font.PLAIN, 20)
        );

        subtitle.setForeground(Color.LIGHT_GRAY);

        gbc.gridy = 1;

        add(subtitle, gbc);

        JPanel buttons = new JPanel();

        buttons.setOpaque(false);

        for (int i = 2; i <= 4; i++) {

            int players = i;

            JButton button =
                new JButton(players + " Players");

            button.setFont(
                new Font("Arial", Font.BOLD, 18)
            );

            button.setPreferredSize(
                new Dimension(150, 50)
            );

            button.addActionListener(e -> {
                frame.startGame(players);
            });

            buttons.add(button);
        }

        gbc.gridy = 2;

        add(buttons, gbc);
    }
}