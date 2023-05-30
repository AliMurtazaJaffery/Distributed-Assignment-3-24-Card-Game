import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class InputFormUsernamePanel extends JPanel {

	private JLabel label;
	private JTextField textField;

	public InputFormUsernamePanel(String fieldName) {
		setLayout(new BorderLayout());


		label = new JLabel(fieldName);
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		textField = new JTextField(20);
		textField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		textField.setMargin(new Insets(5, 10, 5, 10));
		textField.setBackground(Color.WHITE);
		textField.setForeground(Color.BLACK);
		textField.setCaretColor(Color.BLACK);
		textField.setFont(textField.getFont().deriveFont(Font.PLAIN));
		textField.setPreferredSize(new Dimension(200, 30));
		textField.setMaximumSize(new Dimension(200, 30));
		textField.setMinimumSize(new Dimension(200, 30));
		textField.setHorizontalAlignment(SwingConstants.LEFT);


		add(label, BorderLayout.WEST);
		add(textField, BorderLayout.EAST);
	}

	public String getInput() {
		return textField.getText();
	}
}