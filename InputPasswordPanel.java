import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;


public class InputPasswordPanel extends JPanel {

	private JLabel label;
	private JPasswordField pwdField;

	public InputPasswordPanel(String fieldName) {
		setLayout(new BorderLayout());

		label = new JLabel(fieldName);
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		pwdField = new JPasswordField(20);
		pwdField.setEchoChar('*');
		pwdField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		pwdField.setMargin(new Insets(5, 10, 5, 10));
		pwdField.setBackground(Color.WHITE);
		pwdField.setForeground(Color.BLACK);
		pwdField.setCaretColor(Color.BLACK);
		pwdField.setFont(pwdField.getFont().deriveFont(Font.PLAIN));
		pwdField.setPreferredSize(new Dimension(200, 30));
		pwdField.setMaximumSize(new Dimension(200, 30));
		pwdField.setMinimumSize(new Dimension(200, 30));
		pwdField.setHorizontalAlignment(SwingConstants.LEFT);

		add(label, BorderLayout.WEST);
		add(pwdField, BorderLayout.EAST);
	}

	public String getPassword() {
		char[] passwordChars = pwdField.getPassword();
		return new String(passwordChars);
	}
}