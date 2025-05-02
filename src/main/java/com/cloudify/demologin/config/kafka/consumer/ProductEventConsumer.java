package com.cloudify.demologin.config.kafka.consumer;

import com.cloudify.demologin.config.kafka.KafkaConfig;
import com.cloudify.demologin.dto.event.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductEventConsumer {

    @KafkaListener(topics = KafkaConfig.PRODUCT_CREATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeProductCreatedEvent(ProductEvent event) {
        log.info("Received product created event: {}", event);
        // You can add your business logic here
        // For example: send notifications, update caches, sync with other systems, etc.
    }

    @KafkaListener(topics = KafkaConfig.PRODUCT_UPDATED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeProductUpdatedEvent(ProductEvent event) {
        log.info("Received product updated event: {}", event);
        // You can add your business logic here
        // For example: send notifications, update caches, sync with other systems, etc.
    }
} 