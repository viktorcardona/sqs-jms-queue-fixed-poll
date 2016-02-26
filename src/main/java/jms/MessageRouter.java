package jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.ws.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import poller.QueuePollerJmsTemplate;

/**
 * Created by viccardo on 18/02/16.
 */
public class MessageRouter {

    private static final Logger LOGGER = Logger.getLogger(MessageRouter.class);

    private JmsUtil jmsUtil;

    public JmsUtil getJmsUtil() {
        return jmsUtil;
    }

    public void setJmsUtil(JmsUtil jmsUtil) {
        this.jmsUtil = jmsUtil;
    }

    public static final String OPERATION = "Operation";

    public static final String RESENDING_ATTEMPTS = "ResendingAttempts";

    public void routeMessage(Message message) {
        try {
            LOGGER.info("routeMessage: " + message);
            String attributeValue = message.getStringProperty(OPERATION);
            int attempts = message.getIntProperty(RESENDING_ATTEMPTS);
            LOGGER.info("routeMessage.att.Operation: " + attributeValue);
            LOGGER.info("routeMessage.att.attempts: " + attempts);
            LOGGER.info("routeMessage.message: " + message);

            LOGGER.info("routeMessage.(message instanceof TextMessage): " + (message instanceof TextMessage));
            if (message instanceof TextMessage) {
                sendMessage((TextMessage) message);
            }
        } catch (JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    protected int sendMessage(TextMessage msg) throws JMSException {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = msg.getText();
        LOGGER.info("Message " + message);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Message " + message);
        }
            Response response = null;
            String reason = "No reason";
            try {
                response = sendMessageToEndpoint(message);
                status = 500;//response.getStatus();
                if (status < 200 || status >= 300) {
                    //reason = response.getStatusInfo().getReasonPhrase() + response.readEntity(String.class);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                status = HttpStatus.INTERNAL_SERVER_ERROR.value();
                reason = (ex.getMessage() != null ? ex.getMessage() : "Error");
            } finally {
                updateMessageStatus(msg, status, reason);
            }

        return status;
    }

    protected Response sendMessageToEndpoint(String message) {
        return null;
    }


    @Autowired
    private QueuePollerJmsTemplate queuePollerJmsTemplate;

    protected void updateMessageStatus(TextMessage message, int status, String reason) throws JMSException {
        LOGGER.info("updateMessageStatus.status:"+status);
        if (status >= 200 && status < 300) {
            LOGGER.info("Message acknowledge.status:"+status);
            message.acknowledge();
        } else if (status >= 500) {
            LOGGER.info("Message.status.500.so.message.NOT.delete........");
            //message.acknowledge();

            int attempts = message.getIntProperty(RESENDING_ATTEMPTS);

            final int MAX_RETRIES = 5;

            boolean retry = attempts < MAX_RETRIES;
/*
            if(retry) {
                String attributeValue = message.getStringProperty(OPERATION);
                attempts++;
                LOGGER.info("Message.status.500.attempts........" + attempts);
                queuePollerJmsTemplate.sendMessageWithJms(message.getText(), attributeValue, attempts);
                //sendMessageToArchive(message, reason);
            }
            */
        }
    }

    public static void main(String[] argv){
        int attempts = 0;
        System.out.println("Message.attempts........"+attempts);

        test(attempts++);

        System.out.println("Message.attempts........"+attempts);
    }

    public static void test(int attempts){
        System.out.println("Message.test.attempts........"+attempts);
    }

}
