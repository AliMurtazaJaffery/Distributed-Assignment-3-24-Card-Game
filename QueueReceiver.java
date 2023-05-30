import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueReceiver {

	private String host;
	private String connectionFactoryAddress;
	private String queueAddress;
	private Context jndiContext;
	private ConnectionFactory connectionFactory;
	private Queue queue;
	private Connection connection;
	private Session session;
	private MessageConsumer queueReceiver;

	public QueueReceiver(String host, String connectionFactoryAddress, String queueAddress)
			throws NamingException, JMSException {
		this.host = host;
		this.connectionFactoryAddress = connectionFactoryAddress;
		this.queueAddress = queueAddress;

		// Access JNDI
		createJNDIContext();

		// Lookup JMS resources
		lookupConnectionFactory();
		lookupQueue();

		// Create connection->session->sender
		createConnection();

		createSession();
		createReceiver();

	}

	private void createJNDIContext() throws NamingException {
		System.setProperty("org.omg.CORBA.ORBInitialHost", host);
		System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
		try {
			jndiContext = new InitialContext();
		} catch (NamingException e) {
			System.err.println("Could not create JNDI API context: " + e);
			throw e;
		}
	}

	private void lookupConnectionFactory() throws NamingException {

		try {
			connectionFactory = (ConnectionFactory) jndiContext.lookup(connectionFactoryAddress);
		} catch (NamingException e) {
			System.err.println("JNDI API JMS connection factory lookup failed: " + e);
			throw e;
		}
	}

	private void lookupQueue() throws NamingException {

		try {
			queue = (Queue) jndiContext.lookup(queueAddress);
		} catch (NamingException e) {
			System.err.println("JNDI API JMS queue lookup failed: " + e);
			throw e;
		}
	}

	private void createConnection() throws JMSException {
		try {
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException e) {
			System.err.println("Failed to create connection to JMS provider: " + e);
			throw e;
		}
	}

	public Game waitBeforeStartingNewGame(java.sql.Connection conn) throws JMSException, GameException.ServerException {

		int maxNumberOfPlayers = 4;

		ArrayList<User> players = new ArrayList<User>();

		Message m;
		long startTime = 0L;
		long remainingTime = 0L;
		boolean startTimeout = false;

		while (players.size() < maxNumberOfPlayers) {

			if (startTimeout) {
				m = queueReceiver.receive(remainingTime);
				if (m == null) {
					return new Game(players);
				}
			} else {
				m = queueReceiver.receive();
			}

			if (m != null && m instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) m;
				String username = textMessage.getText();
				System.out.println("Received request from " + username);

				if (!User.isUserOnline(username, conn)) {
					continue;
				}

				players.add(User.getUser(username, "", conn));

				System.out.println("Received " + (players.size()) + " players");

				// Game will start When there are 2 players and 10 seconds has passed since the
				// first player joined

				if (players.size() == 1) {
					startTime = System.currentTimeMillis();
				}

				if (players.size() == 2) {

					remainingTime = 10 * 1000L - (System.currentTimeMillis() - startTime);
					if (remainingTime > 0) {
						startTimeout = true;
					} else {
						break;
					}
				}
			}
		}

		return new Game(players);
	}

	public UserAnswer receiveAnswer() throws JMSException {

		Message m = queueReceiver.receive();
		if (m != null && m instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) m;
			System.out.println("Answer recieved");
			return (UserAnswer) objMessage.getObject();
		}

		return null;
	}

	private void createSession() throws JMSException {
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			System.err.println("Failed to create session: " + e);
			throw e;
		}
	}

	private void createReceiver() throws JMSException {
		try {
			queueReceiver = session.createConsumer(queue);
		} catch (JMSException e) {
			System.err.println("Failed to create session: " + e);
			throw e;
		}
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
			}
		}
	}
}
