import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class GameOverPanel extends JPanel {

	    private NavbarPanel navigationPanel;
	    private JPanel mainPanel;
	    private JButton nextGameButton;
	    private UserAnswer currentAnswer;

	    public GameOverPanel(Client client) {
	        currentAnswer = client.getCurrentAnswer();
	        User currentUser = client.getCurrentUser();

	        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	        setLayout(new BorderLayout());
	        setBackground(Color.WHITE);

	        navigationPanel = new NavbarPanel(client);
	        add(navigationPanel, BorderLayout.NORTH);

	        mainPanel = new JPanel();
	        mainPanel.setLayout(new GridLayout(4, 1, 10, 10));
	        mainPanel.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(Color.GRAY, 2),
	                BorderFactory.createEmptyBorder(20, 20, 20, 20)
	        ));
	        mainPanel.setBackground(Color.WHITE);

			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new BorderLayout());
	
			JLabel headerLabel = new JLabel("Game Over");
			headerPanel.add(headerLabel, BorderLayout.CENTER);
			
	        headerPanel.setFont(new Font("Arial", Font.BOLD, 24));
	        headerPanel.setForeground(Color.RED);
	        mainPanel.add(headerPanel);

	        JLabel winnerLabel = new JLabel("Winner: " + currentAnswer.getUsername());
	        winnerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
	        winnerLabel.setForeground(Color.BLACK);
	        mainPanel.add(winnerLabel);
	
	        JLabel expressionLabel = new JLabel("Winning Expression: " + currentAnswer.getUsername());
	        expressionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
	        expressionLabel.setForeground(Color.BLACK);
	        mainPanel.add(expressionLabel);

			JLabel timeLabel = new JLabel("Time to win: " + currentAnswer.getTimeToWin() + " seconds");
			timeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
			timeLabel.setForeground(Color.BLACK);
			mainPanel.add(timeLabel);

			add(mainPanel, BorderLayout.CENTER);

			nextGameButton = new JButton("Next game");
			nextGameButton.setFont(new Font("Arial", Font.BOLD, 18));
			nextGameButton.setBackground(Color.RED);
			nextGameButton.setForeground(Color.WHITE);
			add(nextGameButton, BorderLayout.SOUTH);


			nextGameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (client.getGameServer() != null) {

					SwingWorker<Game, Void> worker = new SwingWorker<Game, Void>() {

						@Override
						protected Game doInBackground() throws Exception {

							client.setPanel(GameState.GAME_JOINING);
							client.setNavigationEnabled(false);
							client.getGameStartQSender().sendMessage(currentUser.getUsername());

							Game game = client.getGamePlayTopicSubscriber().receiveGame();
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

			}

		});
	}
}
