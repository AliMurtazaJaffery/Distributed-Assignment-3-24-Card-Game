import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {

	public ButtonPanel(JButton loginButton, JButton registerButton) {


	    Insets buttonInsets = new Insets(5, 10, 5, 10);
	    loginButton.setMargin(buttonInsets);
	    registerButton.setMargin(buttonInsets);

	    Font buttonFont = new Font("Arial", Font.BOLD, 14);
	    Color buttonBgColor = new Color(75, 170, 250);
	    loginButton.setFont(buttonFont);
	    loginButton.setBackground(buttonBgColor);
	    registerButton.setFont(buttonFont);
	    registerButton.setBackground(buttonBgColor);

	    this.setLayout(new BorderLayout());
	    this.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createLineBorder(Color.GRAY, 1),
	            BorderFactory.createEmptyBorder(10, 30, 10, 30)
	    ));

	    this.add(loginButton, BorderLayout.WEST);
	    this.add(registerButton, BorderLayout.EAST);
	}
}