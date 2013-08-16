package de.galan.plunger.command.hornetq.obsolete;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.commons.lang.StringUtils;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Abstraction for HornetQ Commands that make use of the standardized JMS API
 * 
 * @author daniel
 */
public abstract class AbstractHornetqJmsCommand extends AbstractHornetqCommand {

	private ConnectionFactory factory;
	private Connection connection;
	private Session session;
	private Destination destination;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		try {
			factory = (ConnectionFactory)HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, getTransportConfiguration(pa));
			connection = factory.createConnection(pa.getTarget().getUsername(), pa.getTarget().getPassword());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			if (StringUtils.startsWith(pa.getTarget().getDestination(), "queue.")) {
				destination = HornetQJMSClient.createQueue(pa.getTarget().getShortDestination());
			}
			else if (StringUtils.startsWith(pa.getTarget().getDestination(), "topic.")) {
				destination = HornetQJMSClient.createTopic(pa.getTarget().getShortDestination());
			}
		}
		catch (Exception ex) {
			throw new CommandException("Could not connect to server", ex);
		}
	}


	protected boolean isQueue() {
		return Queue.class.isAssignableFrom(getDestination().getClass());
	}


	protected boolean isTopic() {
		return Topic.class.isAssignableFrom(getDestination().getClass());
	}


	@Override
	protected void close() {
		try {
			if (getSession() != null) {
				getSession().close();
			}
			if (getConnection() != null) {
				getConnection().close();
			}
		}
		catch (JMSException jex) {
			Output.error("Failed to close connection: " + jex.getMessage());
		}
	}


	public Session getSession() {
		return session;
	}


	public ConnectionFactory getFactory() {
		return factory;
	}


	public Connection getConnection() {
		return connection;
	}


	public Destination getDestination() {
		return destination;
	}

}