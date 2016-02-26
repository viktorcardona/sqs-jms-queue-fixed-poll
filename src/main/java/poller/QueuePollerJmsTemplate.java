package poller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import jms.JmsUtil;

/**
 * Created by viccardo on 9/02/16.
 */
@Component
public class QueuePollerJmsTemplate {

    private final static Logger LOGGER = Logger.getLogger(QueuePollerJmsTemplate.class);

    @Resource(name = "jmsTemplate")
    private JmsTemplate jmsTemplate;


    //@Scheduled(cron="0 0/2 * * * *")
    public void readMessage() throws JMSException {
        try {
            LOGGER.info("Reading message at: " + new Date());

            Message msg = jmsTemplate.receive();

            int cont = 1;
            while(msg!=null){
                LOGGER.info("Reading message msg ("+cont+"): "+msg.toString());
                msg = jmsTemplate.receive();
                cont++;
            }

            if (msg instanceof TextMessage) {
                TextMessage txtmsg = (TextMessage) msg;
                LOGGER.info(String.format("Received text: %s", txtmsg.getText()));
            }

            LOGGER.info("Done");

        }catch (JMSException exc){
            exc.printStackTrace();
            LOGGER.error("Error: "+exc.getMessage(), exc);
        }catch (Throwable exc){
            exc.printStackTrace();
            LOGGER.error("Error: "+exc.getMessage(), exc);
        }
    }

    public void sendMessage(String txt){
        jmsTemplate.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                final TextMessage msg = session.createTextMessage(txt);
                msg.setStringProperty("Operation","Leads");
                msg.setIntProperty("Attempts",7);
                msg.setStringProperty("DelaySeconds", "120");
                msg.setStringProperty("DeliveryDelay", "120");
                msg.setStringProperty("Delivery_Delay", "120");
                msg.setStringProperty("DELIVERY_DELAY", "120");
                return msg;
            }
        });
    }

    @Autowired
    private AmazonSQSClient amazonSQSClient;

    @Value("${cloud.aws.sqs.queue.test.url}")
    private String queueTestUrl;

    public void sendMessageWithSQSAPI(String message, String attributeValue, int numberOfAttemps){
        SendMessageRequest request = new SendMessageRequest(queueTestUrl, message);
        Map<String, MessageAttributeValue> messageAttributes = new HashMap();
        messageAttributes.put("Operation",new MessageAttributeValue().withDataType("String").withStringValue(attributeValue));
        messageAttributes.put("Attempts",new MessageAttributeValue().withDataType("Number").withStringValue(String.valueOf(numberOfAttemps)));
        request.withMessageAttributes(messageAttributes);
        request.withDelaySeconds(120);//this delays the message visibility in the queue


        amazonSQSClient.sendMessage(request);
    }

    @Autowired
    private JmsUtil jmsUtil;

    public static final String OPERATION = "Operation";

    public static final String RESENDING_ATTEMPTS = "ResendingAttempts";

    public static final String ERROR_REASON = "ErrorReason";

    public JmsUtil getJmsUtil() {
        return jmsUtil;
    }

    public void setJmsUtil(JmsUtil jmsUtil) {
        this.jmsUtil = jmsUtil;
    }


    public void sendMessageWithJms(String msg, String attributeValue, int numberOfttemps){
        try {
            LOGGER.info("sendMessageWithJms.with.numberOfttemps: "+numberOfttemps);
            TextMessage message = getJmsUtil().getSessionToSend().createTextMessage(msg);
            message.setStringProperty(OPERATION, attributeValue);
            message.setIntProperty(RESENDING_ATTEMPTS, numberOfttemps);
            getJmsUtil().getProducer().send(message);
        } catch (JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

}
