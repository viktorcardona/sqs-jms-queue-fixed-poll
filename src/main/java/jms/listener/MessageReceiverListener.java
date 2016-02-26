package jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jms.MessageRouter;

@Service
public class MessageReceiverListener implements ServletContextListener, MessageListener {

    private static final Logger LOGGER = Logger.getLogger(MessageReceiverListener.class);
    private MessageRouter messageRouter;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("MessageReceiverListener initialization");
        messageRouter = (MessageRouter) WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext()).getBean("messageRouter");
        try {
            messageRouter.getJmsUtil().getConsumer().setMessageListener(this);
            messageRouter.getJmsUtil().getConnection().start();
        } catch (JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("MessageReceiverListener destroy");
        try {
            messageRouter.getJmsUtil().getConnection().close();
        } catch (JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.info("onMessage " + message.getJMSMessageID());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Proccesing messsge " + message.getJMSMessageID());
            }
            messageRouter.routeMessage(message);
        } catch (JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

}
