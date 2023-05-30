import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class UserProfilePanel extends JPanel {

		private NavbarPanel navPanel;
		private JPanel mainPanel;
		private JLabel rankLabel;
	
		public UserProfilePanel(Client client) {
		    User currentUser = client.getCurrentUser();
		    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		    this.setLayout(new BorderLayout());
		    this.setBackground(Color.WHITE);
	
		    navPanel = new NavbarPanel(client);
		    this.add(navPanel, BorderLayout.NORTH);
	

		    GridLayout layout = new GridLayout(5, 1);
		    layout.setVgap(10);
		    mainPanel = new JPanel();
		    mainPanel.setLayout(layout);
		    mainPanel.setBackground(Color.WHITE);
		    mainPanel.setBorder(BorderFactory.createCompoundBorder(
		            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
		            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
	

			headerPanel = new JPanel();
			headerPanel.setLayout(new BorderLayout());

			JLabel headerLabel = new JLabel(currentUser.getUsername());
        	headerLabel.setFont(new Font("Courier", Font.BOLD, 16));

        	headerPanel.add(headerLabel, BorderLayout.CENTER);

		    mainPanel.add(headerPanel);
		    
		    mainPanel.add(createInfoLabel("Number of Wins", currentUser.getWins()));
		    mainPanel.add(createInfoLabel("Number of Games", currentUser.getGames()));
		    mainPanel.add(createInfoLabel("Average Time to Win", currentUser.getTimeToWin()));

		    rankLabel = new JLabel("Rank: ");
		    
		    rankLabel.setFont(new Font("Arial", Font.BOLD, 16));
		    rankLabel.setForeground(Color.DARK_GRAY);
		    mainPanel.add(rankLabel);

		    add(mainPanel, BorderLayout.CENTER);

			SwingWorker<ArrayList<User>, Void> worker = new SwingWorker<ArrayList<User>, Void>() {
	
				@Override
				protected ArrayList<User> doInBackground() throws Exception {
					return (ArrayList<User>) client.getGameServer().getAllUsers();
	
				}
	
				@Override
				protected void done() {
					try {
						ArrayList<User> users = (ArrayList<User>) get();
						client.setAllUsers(users);						
						users.sort(Comparator.comparing(User::getWins, Comparator.reverseOrder()));
						
						User possibleUser;
						int rank = 1;
						for (int i = 0; i < users.size(); i++) {
							possibleUser = users.get(i);
							if (possibleUser != null && possibleUser.getUsername().equals(currentUser.getUsername())) {
								break;
							}
							rank++;
						}
						if (rank > users.size()){
							System.out.println("Couldn't deduce rank of the user");
							rank = 0;
						}
						
						rankLabel.setText("Rank: #" + String.valueOf(rank));
	
					} catch (Exception e) {
						JOptionPane.showMessageDialog(client.getGameFrame(), "Error: " + e.getMessage());
					}
				}
			};
	
			worker.execute();
	}

	
	private JLabel createInfoLabel(String labelText, int value) {
	    JLabel label = new JLabel(labelText + ": " + value);
	    label.setFont(new Font("Arial", Font.PLAIN, 14));
	    label.setForeground(Color.GRAY);
	    return label;
	}
}
