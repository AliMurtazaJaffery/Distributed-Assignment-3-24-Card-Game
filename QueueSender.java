import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueSender {

	private String host;
	private String connectionFactoryAddress;
	private String queueAddress;
	private Context jndiContext;
	private ConnectionFactory connectionFactory;
	private Queue queue;
	private Connection connection;
	private Session session;
	private MessageProducer queueSender;
	
	public QueueSender(String host, String connectionFactoryAddress, String queueAddress)
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

	public void sendMessage(String text) throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText(text);
		queueSender.send(message);
		System.out.println("Sending message " + text);
	}

	public void sendAnswer(UserAnswer answer) throws JMSException {
		ObjectMessage message = session.createObjectMessage(answer);
		queueSender.send(message);
		System.out.println("Sending Answer: "+ answer.getValue());
	}

	private void createSession() throws JMSException {
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			System.err.println("Failed to create session: " + e);
			throw e;
		}
	}


	private void createSender() throws JMSException {
		try {
			queueSender = session.createProducer(queue);
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
