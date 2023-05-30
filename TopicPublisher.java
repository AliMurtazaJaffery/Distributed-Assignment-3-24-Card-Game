import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TopicPublisher {

	private String host;
	private String connectionFactoryAddress;
	private String topicAddress;
	private Context jndiContext;
	private TopicConnectionFactory topicConnectionFactory;
	private Topic topic;
	private TopicConnection connection;
	private TopicSession session;
	private javax.jms.TopicPublisher topicPublisher;
	
	public TopicPublisher(String host, String connectionFactoryAddress, String topicAddress)
			throws NamingException, JMSException {
		this.host = host;
		this.connectionFactoryAddress = connectionFactoryAddress;
		this.topicAddress = topicAddress;

		// Access JNDI
		createJNDIContext();

		// Lookup JMS resources
		lookupConnectionFactory();
		lookupTopic();

		// Create connection->session->sender
		createConnection();

		createSession();
		createSender();

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
			topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup(connectionFactoryAddress);
		} catch (NamingException e) {
			System.err.println("JNDI API JMS connection factory lookup failed: " + e);
			throw e;
		}
	}


	private void lookupTopic() throws NamingException {

		try {
			topic = (Topic) jndiContext.lookup(topicAddress);
		} catch (NamingException e) {
			System.err.println("JNDI API JMS topic lookup failed: " + e);
			throw e;
		}
	}


	private void createConnection() throws JMSException {
		try {
			connection = topicConnectionFactory.createTopicConnection();
			connection.start();
		} catch (JMSException e) {
			System.err.println("Failed to create connection to JMS provider: " + e);
			throw e;
		}
	}



	private void createSession() throws JMSException {
		try {
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			System.err.println("Failed to create session: " + e);
			throw e;
		}
	}



	private void createSender() throws JMSException {
		try {
			topicPublisher = session.createPublisher(topic);
			topicPublisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		} catch (JMSException e) {
			System.err.println("Failed to create session: " + e);
			throw e;
		}
	}
	
	public void sendGame(Game game) throws JMSException {
		ObjectMessage message = session.createObjectMessage(game);
		topicPublisher.publish(message);
		System.out.println("Sending game");
	}

	public void sendAnswer(UserAnswer answer) throws JMSException {
		ObjectMessage message = session.createObjectMessage(answer);
		topicPublisher.publish(message);
		System.out.println("Sending answer");
	}

	public void sendMessage(String text) throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText(text);
		topicPublisher.publish(message);
		System.out.println("Sending message " + text);
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
