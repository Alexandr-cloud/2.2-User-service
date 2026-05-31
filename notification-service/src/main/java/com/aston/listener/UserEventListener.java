package com.aston.listener;

import com.aston.event.UserEvent;
import com.aston.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        logger.info("Received event: {} for user: {}", event.getType(), event.getEmail());

        switch (event.getType()) {
            case CREATED:
                emailService.sendAccountCreatedEmail(event.getEmail());
                break;
            case DELETED:
                emailService.sendAccountDeletedEmail(event.getEmail());
                break;
            default:
                logger.warn("Unknown event type: {}", event.getType());
        }
    }
}