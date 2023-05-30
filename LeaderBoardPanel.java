import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

public class LeaderBoardPanel extends JPanel {

	private NavbarPanel navPanel;
	private JTable leaderBoard;
	private JPanel mainPanel;
	private ArrayList<String[]> data;

	private String[] columnNames = { "Rank", "Player", "Games Won", "Games Played", "Avg Winning Time (s)" };

	public LeaderBoardPanel(Client client) {
		
		ArrayList<User> users = client.getAllUsers();
		
		users.sort(Comparator.comparing(User::getWins, Comparator.reverseOrder()));

		
		
		data = new ArrayList<String[]>();

		int i = 1;
		for (User user : users) {
			String[] userData = { String.valueOf(i), user.getUsername(), String.valueOf(user.getWins()),
					String.valueOf(user.getGames()), String.valueOf(user.getTimeToWin()) };
			data.add(userData);
			i += 1;
		}


		navPanel = new NavbarPanel(client);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());


		mainPanel.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createLineBorder(Color.GRAY, 1),
		    BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		mainPanel.setBackground(Color.WHITE);

		leaderBoard = new JTable(data.toArray(new String[0][0]), columnNames);
		leaderBoard.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		leaderBoard.getTableHeader().setBackground(Color.LIGHT_GRAY);

		leaderBoard.setFont(new Font("Arial", Font.PLAIN, 14));
		leaderBoard.setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(leaderBoard);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setLayout(new BorderLayout());

		this.add(navPanel, BorderLayout.NORTH);
		this.add(mainPanel, BorderLayout.CENTER);
	}
}
