package com.akadimia.microservices.core.user.services;

import com.akadimia.core.event.Event;
import com.akadimia.core.user.User;
import com.akadimia.core.user.UserService;
import com.akadimia.util.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final UserService userService;

    @Autowired
    public MessageProcessor(UserService userService) {
        this.userService = userService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<String, User> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            User user = event.getData();
            LOG.info("Create user with ID: {}", user.getUserId());
            userService.createUser(user);
            break;

        case DELETE:
            String userId = event.getKey();
            LOG.info("Delete user with ID: {}", userId);
            userService.deleteUser(userId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
