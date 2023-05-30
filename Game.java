import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class Game implements Serializable {
	private ArrayList<User> players;
	private Card[] pokerCards;
	private UUID gameID;

	public ArrayList<User> getPlayers() {
		return players;
	}

	public Card[] getPokerCards() {
		return pokerCards;
	}

	public UUID getGameID() {
		return gameID;
	}

	public Game(ArrayList<User> players) {
		this.players = players;
		this.pokerCards = getCards();
		this.gameID = UUID.randomUUID();
	}

	private Card[] getCards() {
		ArrayList<String> allCards = Card.getAllCards();
		Collections.shuffle(allCards);
		return new Card[] { new Card(allCards.get(0)), new Card(allCards.get(1)),
				new Card(allCards.get(2)), new Card(allCards.get(3)) };
	}

	public void updatePlayerStatistics(UserAnswer answer, Connection conn) throws GameException.ServerException {
		String winner = answer.getUsername();
		for (User player : players) {
			player.setGames(player.getGames() + 1);
			if (player.getUsername().equals(winner)) {
				player.setTimeToWin(
						(player.getTimeToWin() * player.getWins() + answer.getTimeToWin()) / (player.getWins() + 1));
				player.setWins(player.getWins() + 1);

			}
			User.updateUser(player, conn);
		}
	}

	public boolean containsPlayer(User user) {
		for (User player : players) {
			if (player.getUsername().equals(user.getUsername())) {
				return true;
			}
		}
		return false;
	}

}
