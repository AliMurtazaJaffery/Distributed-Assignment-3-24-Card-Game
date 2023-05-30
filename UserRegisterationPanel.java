import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class UserRegisterationPanel extends JPanel {

	private Client client;

	private JPanel headerPanel;

	private JPanel formPanel;
	private InputFormUsernamePanel nameInput;
	private InputPasswordPanel passwordInput;
	private InputPasswordPanel confirmPasswordInput;

	private ButtonPanel buttonPanel;
	private JButton loginButton;
	private JButton registerButton;

	public UserRegisterationPanel(Client client) {

	        this.client = client;

	        this.setLayout(new BorderLayout());
	        this.setBackground(new Color(255, 255, 204));

			headerPanel = new JPanel();
			headerPanel.setLayout(new BorderLayout());
	
			JLabel headerLabel = new JLabel("Register");
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
	        formPanel.setLayout(new GridLayout(3, 1));
	        nameInput = new InputFormUsernamePanel("Login Name: ");
	        passwordInput = new InputPasswordPanel("Password: ");
	        confirmPasswordInput = new InputPasswordPanel("Confirm Password: ");
	        formPanel.add(nameInput);
	        formPanel.add(passwordInput);
	        formPanel.add(confirmPasswordInput);
	        formPanel.setBackground(new Color(224, 224, 224));
	        formPanel.setBorder(BorderFactory.createCompoundBorder(
	        	    BorderFactory.createLineBorder(Color.BLACK, 1),
	        	    BorderFactory.createCompoundBorder(
	        	        BorderFactory.createEmptyBorder(10, 10, 10, 10), 
	        	        BorderFactory.createCompoundBorder(
	        	            BorderFactory.createLineBorder(Color.BLACK, 1), 
	        	            BorderFactory.createLineBorder(Color.WHITE, 10) 
	        	        )
	        	    )
	        	));
	        
	        this.add(formPanel, BorderLayout.CENTER);


	        loginButton = new JButton("Login");
	        registerButton = new JButton("Register");
	        buttonPanel = new ButtonPanel(loginButton, registerButton);
	        this.add(buttonPanel, BorderLayout.SOUTH);

		loginButton.addActionListener( e ->  {
				client.setPanel(GameState.LOGIN);
			}

		);

		registerButton.addActionListener(e -> {
				String username = nameInput.getInput();
				String password = passwordInput.getPassword();
				String confirmPassword = confirmPasswordInput.getPassword();

				
				if (username.length() == 0) {
				    JOptionPane.showMessageDialog(client.getGameFrame(), "Error: Username field is required!");
				    return;
				}
	
				if (password.length() == 0) {
				    JOptionPane.showMessageDialog(client.getGameFrame(), "Error: Password field is required!");
				    return;
				}
				if (confirmPassword.length() == 0) {
				    JOptionPane.showMessageDialog(client.getGameFrame(), "Error: Confirm Password field is required!");
				    return;
				}


				if (!password.equals(confirmPassword)) {

					JOptionPane.showMessageDialog(client.getGameFrame(), "Error: Passwords are not matching!");
					return;
				}

				if (client.getGameServer() != null) {

					SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {

						@Override
						protected User doInBackground() throws Exception {
							return (User) client.getGameServer().register(username, password);
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

