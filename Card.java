import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Card implements Serializable {
	private String iconUrl;
	private int value;

	
	public Card(String cardId) {
		value = Integer.parseInt(cardId.substring(1));
		iconUrl = "Images/card_" + cardId + ".gif";
	}
	
	public ImageIcon getIcon() {
		return new ImageIcon(iconUrl);
	}

	public int getValue() {
		return value;
	}

	public static ArrayList<String> getAllCards() {
		ArrayList<String> allCards = new ArrayList<>();
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 13; j++) {
				String id = Integer.toString(i) + Integer.toString(j);
				allCards.add(id);
			}
		}
		return allCards;
	}
}