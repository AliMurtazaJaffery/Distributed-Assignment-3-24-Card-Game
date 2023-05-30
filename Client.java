import java.awt.event.*;
import java.rmi.registry.*;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.swing.*;

public class Client {

	private JFrame gameFrame;
	private ServerInterface gameserver;
	private User currentUser;
	private ArrayList<User> allUsers;
	private boolean navigationEnabled = true;

	private QueueSender gameStartQSender;
	private QueueSender gamePlayQSender;
	private TopicSubscriber gamePlayTopicSubscriber;
	private TopicSubscriber answerTopicSubscriber;

	private Game currentGame;
	private UserAnswer currentAnswer;

	public JFrame getGameFrame() {
		return gameFrame;
	}

	public ServerInterface getGameServer() {
		return gameserver;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User user) {
		currentUser = user;
	}

	public ArrayList<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(ArrayList<User> users) {
		allUsers = users;
	}

	public boolean getNavigationEnabled() {
		return navigationEnabled;
	}

	public void setNavigationEnabled(boolean b) {
		navigationEnabled = b;
	}

	public void setGameStartQSender(QueueSender qs) {
		gameStartQSender = qs;
	}

	public QueueSender getGameStartQSender() {
		return gameStartQSender;
	}

	public void setGamePlayQSender(QueueSender qs) {
		gamePlayQSender = qs;
	}

	public QueueSender getGamePlayQSender() {
		return gamePlayQSender;
	}

	public TopicSubscriber getGamePlayTopicSubscriber() {
		return gamePlayTopicSubscriber;
	}

	public void setGamePlayTopicSubscriber(TopicSubscriber ts) {
		gamePlayTopicSubscriber = ts;
	}

	public TopicSubscriber getAnswerTopicSubscriber() {
		return answerTopicSubscriber;
	}

	public void setAnswerTopicSubscriber(TopicSubscriber ts) {
		answerTopicSubscriber = ts;
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game game) {
		currentGame = game;
	}

	public UserAnswer getCurrentAnswer() {
		return currentAnswer;
	}

	public void setCurrentAnswer(UserAnswer answer) {
		currentAnswer = answer;
	}

	public static void main(String[] args) {
		Client app = new Client(args[0]);
		try {
			app.setGameStartQSender(new QueueSender(args[0], "jms/GameStartConnectionFactory", "jms/GameStartQueue"));
			app.setGamePlayQSender(new QueueSender(args[0], "jms/GamePlayConnectionFactory", "jms/GamePlayQueue"));
			app.setGamePlayTopicSubscriber(
					new TopicSubscriber(args[0], "jms/GamePlayConnectionFactory", "jms/GamePlayTopic"));
			app.setAnswerTopicSubscriber(
					new TopicSubscriber(args[0], "jms/AnswerConnectionFactory", "jms/AnswerTopic"));
			app.go();
		} catch (NamingException | JMSException e) {
			System.err.println("Program aborted");
		}
	}

	public Client(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			gameserver = (ServerInterface) registry.lookup("24-game-server");
		} catch (Exception e) {
			System.err.println("Failed accessing RMI: " + e);
		}
	}

	public void go() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				generateGUI();
			}
		});
	}

	public void setPanel(GameState panel) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getGameFrame().setContentPane(getPanel(panel));
				getGameFrame().invalidate();
				getGameFrame().validate();
				getGameFrame().pack();
			}
		});
	}

	public void generateGUI() {
		Client client = this;
		gameFrame = new JFrame("24 Game");
		gameFrame.add(getPanel(GameState.LOGIN));
		gameFrame.pack();
		gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		gameFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (client.getGameServer() != null && client.getCurrentUser() != null) {
					try {
						client.getGameServer().logout(client.getCurrentUser());
					} catch (Exception e) {
					}
				}
				gameFrame.dispose();

				if (client.getGameStartQSender() != null) {
					try {
						System.out.println("Closing GameStart Connection");
						client.getGameStartQSender().close();

					} catch (Exception e) {
					}
				}

				if (client.getGamePlayQSender() != null) {
					try {
						System.out.println("Closing GamePlay Q Connection");
						client.getGamePlayQSender().close();

					} catch (Exception e) {
					}
				}

				if (client.getGamePlayTopicSubscriber() != null) {
					try {
						System.out.println("Closing Game Play Topic Connection");
						client.getGamePlayTopicSubscriber().close();

					} catch (Exception e) {
					}
				}

				if (client.getAnswerTopicSubscriber() != null) {
					try {
						System.out.println("Closing Game Play Topic Connection");
						client.getAnswerTopicSubscriber().close();

					} catch (Exception e) {
					}
				}

				System.exit(0);
			}
		});
		getGameFrame().setVisible(true);
	}

	public JPanel getPanel(GameState panel) {
		switch (panel) {
			case LOGIN:
				return new UserLoginPanel(this);
			case REGISTER:
				return new UserRegisterationPanel(this);
			case PROFILE:
				return new UserProfilePanel(this);
			case GAME_NEW:
				return new InitiateNewGamePanel(this);
			case GAME_JOINING:
				return new PlayerWaitingGamePanel(this);
			case GAME_PLAYING:
				return new PlayingGamePanel(this);
			case GAME_OVER:
				return new GameOverPanel(this);
			case LEADERBOARD:
				return new LeaderBoardPanel(this);
			default:
				return null;
		}
	}

}
