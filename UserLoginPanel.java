import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class UserLoginPanel extends JPanel {

	private Client client;
	private Jpanel headerPanel;
	private JPanel formPanel;
	private InputFormUsernamePanel nameInput;
	private InputPasswordPanel passwordInput;
	private ButtonPanel buttonPanel;
	private JButton loginButton;
	private JButton registerButton;

	public UserLoginPanel(Client client) {
		this.client = client;

		this.setLayout(new BorderLayout());
		this.setBackground(new Color(255, 255, 204));

		headerPanel = new JPanel();
		headerPanel.setLayout(new BorderLayout());

		JLabel headerLabel = new JLabel("Login");
        headerLabel.setFont(new Font("Courier", Font.BOLD, 16));

        headerPanel.add(headerLabel, BorderLayout.CENTER);

		headerPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		headerPanel.setBackground(new Color(204, 204, 204));
		headerPanel.setForeground(Color.BLACK);
		headerPanel.setFont(new Font("Arial", Font.BOLD, 24));
		
		this.add(headerPanel, BorderLayout.NORTH);

		formPanel = new JPanel();
		formPanel.setLayout(new GridLayout(2, 1));
		nameInput = new InputFormUsernamePanel("Login Name: ");
		passwordInput = new InputPasswordPanel("Password: ");
		formPanel.add(nameInput);
		formPanel.add(passwordInput);
		formPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		formPanel.setBackground(new Color(204, 204, 204));
		this.add(formPanel, BorderLayout.CENTER);

		loginButton = new JButton("Login");
		loginButton.setBackground(new Color(204, 204, 204));
		loginButton.setForeground(Color.BLACK);
		loginButton.setFocusPainted(false);
		loginButton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		loginButton.setFont(new Font("Arial", Font.BOLD, 16));

		registerButton = new JButton("Register");
		registerButton.setBackground(new Color(204, 204, 204));
		registerButton.setForeground(Color.BLACK);
		registerButton.setFocusPainted(false);
		registerButton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		registerButton.setFont(new Font("Arial", Font.BOLD, 16));

		buttonPanel = new ButtonPanel(loginButton, registerButton);
		buttonPanel.setBackground(new Color(204, 204, 204));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(buttonPanel, BorderLayout.SOUTH);

		registerButton.addActionListener(e -> {
			
			client.setPanel(GameState.REGISTER);
		

		});
		
		loginButton.addActionListener(e -> {
				String username = nameInput.getInput();
				String password = passwordInput.getPassword();
	
				if (username.length() == 0) {
				    JOptionPane.showMessageDialog(client.getGameFrame(), "Error: Username field is required!");
				    return;
				}
	
				if (password.length() == 0) {
				    JOptionPane.showMessageDialog(client.getGameFrame(), "Error: Password field is required!");
				    return;
				}
				
				if (client.getGameServer() != null) {

					SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {

						@Override
						protected User doInBackground() throws Exception {
							return (User) client.getGameServer().login(username, password);
						}

						@Override
						protected void done() {
							try {
								User user = (User) get();
								client.setCurrentUser(user);
								client.setPanel(GameState.PROFILE);
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
