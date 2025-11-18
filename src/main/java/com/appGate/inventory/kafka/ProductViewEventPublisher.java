package com.appGate.inventory.kafka;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class ProductViewEventPublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "product-topic";

    public void publishOrderEvent(Long productId) {
        kafkaTemplate.send(TOPIC, productId.toString());
    }
}