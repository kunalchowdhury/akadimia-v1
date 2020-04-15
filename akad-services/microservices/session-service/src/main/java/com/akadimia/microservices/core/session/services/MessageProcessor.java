package com.akadimia.microservices.core.session.services;

import com.akadimia.core.event.Event;
import com.akadimia.core.session.Session;
import com.akadimia.core.session.SessionService;
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

    private final SessionService sessionService;

    @Autowired
    public MessageProcessor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<String, Session> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Session session = event.getData();
            LOG.info("Create Session with ID: {}", session.getUserId());
            sessionService.createSession(session);
            break;

        case DELETE:
            String sessionId = event.getKey();
            LOG.info("Delete Session with ID: {}", sessionId);
            sessionService.deleteSession(sessionId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
