package com.ecommerce.infra.event.bus;

import com.ecommerce.domain.event.EventBus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

@Component
public class KafkaEventBus implements EventBus {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Map<String, List<Consumer<String>>> subscribers;

    public KafkaEventBus(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.subscribers = new HashMap<>();
    }

    @Override
    public void publish(String topic, String event) {
        kafkaTemplate.send(topic, event);
        if (subscribers.containsKey(topic)) {
            subscribers.get(topic).forEach(subscriber -> subscriber.accept(event));
        }
    }

    @Override
    public void subscribe(String topic, Consumer<String> eventHandler) {
        subscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(eventHandler);
    }

    @KafkaListener(topics = "#{'${kafka.topics}'.split(',')}", groupId = "${kafka.group-id}")
    public void consumeEvent(ConsumerRecord<String, String> record, @Payload String event) {
        String topic = record.topic();
        if (subscribers.containsKey(topic)) {
            subscribers.get(topic).forEach(subscriber -> subscriber.accept(event));
        }
    }
}