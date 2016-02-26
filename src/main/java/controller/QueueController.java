package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import poller.QueuePollerJmsTemplate;

/**
 * Created by viccardo on 10/02/16.
 */
@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private QueuePollerJmsTemplate queuePollerJmsTemplate;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public String sendMessage(@RequestParam String message) {
        queuePollerJmsTemplate.sendMessageWithJms(message, "Leads", 0);
        return "{ message: sent}";
    }
}
