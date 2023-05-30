import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class InitiateNewGamePanel extends JPanel {
	private JButton newGameButton;
	private NavbarPanel navbarPanel;
	private Client client;

	public InitiateNewGamePanel(Client client) {
		this.client = client;
		User user = client.getCurrentUser();

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setLayout(new BorderLayout());

		navbarPanel = new NavbarPanel(client);
		this.add(navbarPanel, BorderLayout.NORTH);

		newGameButton = new JButton("New Game");
		newGameButton.setPreferredSize(new Dimension(100, 60));
		newGameButton.setBackground(new Color(204, 204, 204));
		newGameButton.setForeground(Color.BLACK);
		newGameButton.setFocusPainted(false);
		newGameButton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
		
		
		this.add(newGameButton, BorderLayout.CENTER);

		newGameButton.addActionListener(e-> {
				if (client.getGameServer() != null) {

					SwingWorker<Game, Void> worker = new SwingWorker<Game, Void>() {

						@Override
						protected Game doInBackground() throws Exception {
							Game game = null;
							
							boolean waiting = true;
							
							
							client.setPanel(GameState.GAME_JOINING);
							client.setNavigationEnabled(false);
							client.getGameStartQSender().sendMessage(user.getUsername());
							
							while (waiting) {
								game = client.getGamePlayTopicSubscriber().receiveGame();
								if (game.containsPlayer(user)) {
									break;
								}
							}
							return game;
						}

						@Override
						protected void done() {
							try {

								Game game = (Game) get();
								client.setCurrentGame(game);
								client.setPanel(GameState.GAME_PLAYING);
								
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
