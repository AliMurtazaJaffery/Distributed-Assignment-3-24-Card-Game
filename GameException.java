import java.io.Serializable;

public class GameException implements Serializable  {
	
	public class ClientException extends Exception {
		public ClientException(String message) {
			super(message);
		}
	}

	
	public class ServerException extends Exception {
		public ServerException(String message) {
			super(message);
		}
	}
}
