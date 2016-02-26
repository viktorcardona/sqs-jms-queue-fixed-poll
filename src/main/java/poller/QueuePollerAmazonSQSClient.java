package poller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

/**
 * Created by viccardo on 10/02/16.
 */
@Component
public class QueuePollerAmazonSQSClient {

    private final static Logger LOGGER = Logger.getLogger(QueuePollerAmazonSQSClient.class);


    @Value("${aws.region.name}")
    private String region;


    @Value("${cloud.aws.sqs.queue.test.url}")
    private String queueTestUrl;

    @Autowired
    private AmazonSQSClient amazonSQSClient;

    @PostConstruct
    public void postConstruct(){
        amazonSQSClient.setRegion(Region.getRegion(Regions.fromName(region)));
    }

    //@Scheduled(cron="0 * * * * *")
    public void processMessages(){

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueTestUrl);
        receiveMessageRequest.withMessageAttributeNames("Operation","Attempts");

        //check: http://stackoverflow.com/questions/19792881/why-do-sqs-messages-sometimes-remain-in-flight-on-queue
        //wait time seconds
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.setWaitTimeSeconds(20);
        LOGGER.info("processMessages.getMaxNumberOfMessages: "+receiveMessageRequest.getMaxNumberOfMessages());

        List<com.amazonaws.services.sqs.model.Message> messages = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages();


        int numberOfMessagesRetrieved = (messages!=null)?messages.size():0;

        int counter = 1;

        while(numberOfMessagesRetrieved>0){

            LOGGER.info("Counter:::"+counter+":::Number of messages received: "+numberOfMessagesRetrieved);

            for (com.amazonaws.services.sqs.model.Message message : messages) {
                LOGGER.info("(message instanceof TextMessage): "+(message instanceof TextMessage));
                if(message instanceof TextMessage){
                    TextMessage theMsg = (TextMessage)message;
                }

                LOGGER.info("Message: " + message.getBody());

                for (Map.Entry<String, MessageAttributeValue> entry : message.getMessageAttributes().entrySet()) {
                    LOGGER.info("  Msg Attribute");
                    LOGGER.info("    Name:  " + entry.getKey());
                    LOGGER.info("    Value: " + entry.getValue().getStringValue());
                    LOGGER.info("    Type: " + entry.getValue().getDataType());
                }

                for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                    LOGGER.info("  Attribute");
                    LOGGER.info("    Name:  " + entry.getKey());
                    LOGGER.info("    Value: " + entry.getValue());
                }
                String messageReceiptHandle = message.getReceiptHandle();
                amazonSQSClient.deleteMessage(new DeleteMessageRequest(queueTestUrl, messageReceiptHandle));
                LOGGER.info("Message deleted.");
            }

            counter++;
            messages = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages();
            numberOfMessagesRetrieved = (messages!=null)?messages.size():0;
        }


    }
}
