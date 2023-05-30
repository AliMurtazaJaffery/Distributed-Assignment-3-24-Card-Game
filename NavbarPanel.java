import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;



public class NavbarPanel extends JPanel {

    private JButton profileButton;
    private JButton gameButton;
    private JButton leaderBoardButton;
    private JButton logoutButton;

    public NavbarPanel(Client client) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        Border buttonBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        Color buttonColor = new Color(204, 204, 204);

        profileButton = new JButton("User Profile");
        profileButton.setBorder(buttonBorder);
        profileButton.setBackground(buttonColor);

        gameButton = new JButton("Play Game");
        gameButton.setBorder(buttonBorder);
        gameButton.setBackground(buttonColor);

        leaderBoardButton = new JButton("Leader Board");
        leaderBoardButton.setBorder(buttonBorder);
        leaderBoardButton.setBackground(buttonColor);

        logoutButton = new JButton("Logout");
        logoutButton.setBorder(buttonBorder);
        logoutButton.setBackground(buttonColor);

        c.gridy = 0;
        c.gridx = 0;
        
        this.add(profileButton, c);

        c.gridx = 1;
        this.add(gameButton, c);

        c.gridx = 2;
        this.add(leaderBoardButton, c);
        
        
        c.gridx = 3;
        c.weighty = 1.0;
        this.add(logoutButton, c);


        profileButton.addActionListener(e -> {
            if (client.getNavigationEnabled()) {
                client.setPanel(GameState.PROFILE);
            }
        });

        gameButton.addActionListener(e -> {
            if (client.getNavigationEnabled()) {
                client.setPanel(GameState.GAME_NEW);
            }
        });

		leaderBoardButton.addActionListener(e -> {

				if (client.getGameServer() != null && client.getNavigationEnabled()) {

					SwingWorker<ArrayList<User>, Void> worker = new SwingWorker<ArrayList<User>, Void>() {

						@Override
						protected ArrayList<User> doInBackground() throws Exception {
							return (ArrayList<User>) client.getGameServer().getAllUsers();

						}

						@Override
						protected void done() {
							try {
								ArrayList<User> users = (ArrayList<User>) get();
								
								if (users == null) {
			                        System.out.println("Failed to retrieve users from server.");
			                    }
								client.setAllUsers(users);
								client.setPanel(GameState.LEADERBOARD);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(client.getGameFrame(), "Error: " + e.getMessage());
							}
						}
					};

					worker.execute();
				}

		});

		logoutButton.addActionListener(e -> {

				if (client.getGameServer() != null && client.getNavigationEnabled()) {

					SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {
							client.getGameServer().logout(client.getCurrentUser());
							return null;
						}

						@Override
						protected void done() {
							try {
								client.setCurrentUser(null);
								client.setPanel(GameState.LOGIN);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(client.getGameFrame(), "Error: " + e.getMessage());
							}
						}
					};

					worker.execute();
				}
		});
	}
}