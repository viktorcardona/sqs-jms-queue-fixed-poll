package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.log4j.Logger;

import jms.configuration.JmsConfiguration;

public class JmsUtil {

    private static final Logger LOGGER = Logger.getLogger(JmsUtil.class);
    private JmsConfiguration jmsConfiguration;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session sessionToReceive;
    private MessageConsumer consumer;
    private Session sessionToSend;
    private MessageProducer producer;

    public JmsConfiguration getJmsConfiguration() {
        return jmsConfiguration;
    }

    public void setJmsConfiguration(JmsConfiguration jmsConfiguration) {
        this.jmsConfiguration = jmsConfiguration;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSessionToReceive() {
        return sessionToReceive;
    }

    public void setSessionToReceive(Session sessionToReceive) {
        this.sessionToReceive = sessionToReceive;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    public Session getSessionToSend() {
        return sessionToSend;
    }

    public void setSessionToSend(Session sessionToSend) {
        this.sessionToSend = sessionToSend;
    }

    public MessageProducer getProducer() {
        return producer;
    }

    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

    public MessageConsumer createConsumer(Connection connection, String queueName) throws JMSException {
        sessionToReceive = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        return sessionToReceive.createConsumer(sessionToReceive.createQueue(queueName));
    }

    public MessageProducer createProducer(Connection connection, String queueName) throws JMSException {
        sessionToSend = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        return sessionToSend.createProducer(sessionToSend.createQueue(queueName));
    }

    public void init() throws JMSException {
        connection = connectionFactory.createConnection();
        consumer = createConsumer(connection, jmsConfiguration.getQueueName());
        producer = createProducer(connection, jmsConfiguration.getQueueName());
        LOGGER.info("JmsUtil init configuration producer " + producer);
    }

}
