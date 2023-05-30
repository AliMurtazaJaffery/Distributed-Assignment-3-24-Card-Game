import java.io.Serializable;
import java.util.UUID;

public class UserAnswer implements Serializable {
	
	
	private Double value;
	private String username;
	private int timeToWin;
	private UUID gameID;
	private String expression;


	public UserAnswer(UUID gameID, String expression, Double value, String username, int timeToWin) {
		this.gameID = gameID;
		this.expression = expression;
		this.value = value;
		this.username = username;
		this.timeToWin = timeToWin;
	}
	
	public UUID getGameID() {
		return gameID;
	}

	public Double getValue() {
		return value;
	}

	public String getUsername() {
		return username;
	}
	public String getExpression() {
		return expression;
	}



	public int getTimeToWin() {
		return timeToWin;
	}


}
