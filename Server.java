import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;

public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final String DB_HOST = "localhost";
	private static final String DB_USER = "c3358";
	private static final String DB_PASS = "c3358PASS";
	private static final String DB_NAME = "c3358";

	private Connection conn;
	private QueueReceiver gameStartQReceiver;
	private QueueReceiver gamePlayQReceiver;
	private TopicPublisher gamePlayTopicPublisher;
	private TopicPublisher answerTopicPublisher;

	public QueueReceiver getGameStartQReceiver() {
		return gameStartQReceiver;
	}

	public void setGameStartQReceiver(QueueReceiver qr) {
		gameStartQReceiver = qr;
	}

	public QueueReceiver getGamePlayQReceiver() {
		return gamePlayQReceiver;
	}

	public void setGamePlayQReceiver(QueueReceiver qr) {
		gamePlayQReceiver = qr;
	}

	public TopicPublisher getGamePlayTopicPublisher() {
		return gamePlayTopicPublisher;
	}

	public void setGamePlayTopicPublisher(TopicPublisher tp) {
		gamePlayTopicPublisher = tp;
	}

	public TopicPublisher getAnswerTopicPublisher() {
		return answerTopicPublisher;
	}

	public void setAnswerTopicPublisher(TopicPublisher tp) {
		answerTopicPublisher = tp;
	}

	public static void main(String[] args) {
		String host = "localhost";
		QueueReceiver gsqr = null;
		QueueReceiver gpqr = null;
		TopicPublisher gptp = null;
		TopicPublisher atp = null;
		try {

			System.setProperty("java.rmi.server.hostname", "localhost");
			// System.setProperty("java.security.policy","/Users/alimurtaza/eclipse-workspace/Assignment3/security.policy");
			Server app = new Server();
			Naming.rebind("24-game-server", app);
			System.out.println("Game Server registered");

			gsqr = new QueueReceiver(host, "jms/GameStartConnectionFactory", "jms/GameStartQueue");
			gpqr = new QueueReceiver(host, "jms/GamePlayConnectionFactory", "jms/GamePlayQueue");
			gptp = new TopicPublisher(host, "jms/GamePlayConnectionFactory", "jms/GamePlayTopic");
			atp = new TopicPublisher(host, "jms/AnswerConnectionFactory", "jms/AnswerTopic");
			app.setGameStartQReceiver(gsqr);
			app.setGamePlayQReceiver(gpqr);
			app.setGamePlayTopicPublisher(gptp);
			app.setAnswerTopicPublisher(atp);

			int i = 0;
			while (true) {
				System.out.println("Waiting for game " + i);
				Game game = app.getGameStartQReceiver().waitBeforeStartingNewGame(app.conn);
				// start game
				app.getGamePlayTopicPublisher().sendGame(game);

				// while answer incorrect
				while (true) {
					UserAnswer answer = app.getGamePlayQReceiver().receiveAnswer();
					if (answer.getValue() == 24) {
						app.getAnswerTopicPublisher().sendAnswer(answer);
						game.updatePlayerStatistics(answer, app.conn);
						break;
					}
				}

				i++;
			}

		} catch (Exception ex) {
			System.err.println("Exception thrown in main: " + ex);
			ex.printStackTrace();
		} finally {
			if (gsqr != null) {
				try {
					gsqr.close();
				} catch (Exception e) {
				}
			}
			if (gpqr != null) {
				try {
					gpqr.close();
				} catch (Exception e) {
				}
			}
			if (gptp != null) {
				try {
					gpqr.close();
				} catch (Exception e) {
				}
			}
			if (atp != null) {
				try {
					gpqr.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public Server() throws RemoteException, GameException.ServerException {
		super();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + DB_HOST + "/" + DB_NAME + "?user=" + DB_USER + "&password=" + DB_PASS);
			System.out.println("Database connection successful.");
			User.clearOnlineUsers(conn);

		} catch (Exception ex) {
			System.err.println("Exception thrown: " + ex);
			throw new GameException().new ServerException("Server Exception");
		}
	}

	@Override
	public User login(String username, String password)
			throws RemoteException, GameException.ClientException, GameException.ServerException {

		try {
			User user = null;

			if (User.isUserOnline(username, conn)) {
				throw new GameException().new ClientException("User already logged in!");
			}

			user = User.getUser(username, password, conn);
			if (user == null) {
				throw new GameException().new ClientException("Incorrect username or password");
			}

			User.addOnlineUser(username, conn);
			return user;
		} catch (GameException.ClientException ex) {
			throw ex;
		} catch (Exception ex) {
			System.err.println("Exception thrown: " + ex);
			throw new GameException().new ServerException("Server Exception");
		}
	}

	@Override
	public User register(String username, String password)
			throws RemoteException, GameException.ClientException, GameException.ServerException {

		try {
			if (User.getUser(username, "", conn) != null) {
				throw new GameException().new ClientException("Username already exists!");
			}

			User user = new User(username);
			User.registerUser(user, password, conn);
			User.addOnlineUser(username, conn);

			return user;
		} catch (GameException.ClientException ex) {
			throw ex;
		} catch (Exception ex) {
			System.err.println("Exception thrown: " + ex);
			throw new GameException().new ServerException("Server Exception");
		}

	}

	@Override
	public void logout(User user) throws RemoteException, GameException.ServerException {
		try {

			if (!User.isUserOnline(user.getUsername(), conn)) {
				throw new GameException().new ClientException("User already logged out!");
			}

			User.removeOnlineUser(user.getUsername(), conn);
		} catch (Exception ex) {
			System.err.println("Exception thrown: " + ex);
			throw new GameException().new ServerException("Server Exception");
		}
	}

	@Override
	public ArrayList<User> getAllUsers() throws RemoteException, GameException.ServerException {
		try {
			return User.getAllUsers(conn);
		} catch (Exception ex) {
			System.err.println("Exception thrown: " + ex);
			throw new GameException().new ServerException("Server Exception");
		}
	}

}
