import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerWaitingGamePanel extends JPanel {

    private NavbarPanel navigationPanel;
    private JLabel waitingLabel;

    public PlayerWaitingGamePanel(Client client) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        navigationPanel = new NavbarPanel(client);
        add(navigationPanel, BorderLayout.NORTH);

        waitingLabel = new JLabel("Waiting for players...");
        waitingLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        waitingLabel.setForeground(Color.BLACK);
        waitingLabel.setHorizontalAlignment(JLabel.CENTER);
        waitingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(waitingLabel, BorderLayout.CENTER);
    }
}