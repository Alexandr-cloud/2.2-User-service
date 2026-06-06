package com.aston.service;

import com.aston.event.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String TOPIC = "user-events";

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendUserEvent(UserEvent event) {
        kafkaTemplate.send(TOPIC, event);
        logger.info("Event sent to Kafka: {} for user email: {}", event.getType(), event.getEmail());
    }
}
