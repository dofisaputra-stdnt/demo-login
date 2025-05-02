package com.cloudify.demologin.config.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    @KafkaListener(topics = "demo-login-topic", groupId = "demo-login-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}