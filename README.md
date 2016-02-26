# sqs-jms-queue-fixed-poll

Web App on Spring

Connection to Amazon AWS SQS queue
Fixed poll QueuePollerAmazonSQSClient. Enable cron scheduler.
Fixed poll QueuePollerJmsTemplate. Enable cron scheduler. 

Connection to SQS queue through JMS.

At the end the re-processing of messages of a SQS queue is done with the attributes:

'Message Retention Period' in 8 hours
'Default Visibility Timeout' in 10 mins, after a message is delivered the message is not visible 4 10 mins, at least the message is acknowledged (deleted)


Endpoint to send a message:

http://localhost:8080/sqs-queue-fixed-poll-1.0-SNAPSHOT/queue/send?message={%22the-message%22:%22hello%20world-Earth%22}

Tools:

JDK 1.8
Tomcat 7



