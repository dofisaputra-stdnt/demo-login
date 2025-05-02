package com.cloudify.demologin.config.kafka.producer;

import com.cloudify.demologin.config.kafka.KafkaConfig;
import com.cloudify.demologin.dto.event.ProductEvent;
import com.cloudify.demologin.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProductCreatedEvent(Product product) {
        ProductEvent event = buildProductEvent(product, "CREATED");
        log.info("Sending product created event: {}", event);
        kafkaTemplate.send(KafkaConfig.PRODUCT_CREATED_TOPIC, event);
    }

    public void sendProductUpdatedEvent(Product product) {
        ProductEvent event = buildProductEvent(product, "UPDATED");
        log.info("Sending product updated event: {}", event);
        kafkaTemplate.send(KafkaConfig.PRODUCT_UPDATED_TOPIC, event);
    }

    private ProductEvent buildProductEvent(Product product, String eventType) {
        return ProductEvent.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .build();
    }
} 