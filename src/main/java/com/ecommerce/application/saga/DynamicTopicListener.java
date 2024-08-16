package com.ecommerce.application.saga;

import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.event.OrderQueryResultEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Component
public class DynamicTopicListener {
    private final ObjectMapper objectMapper;
    private final Map<String, CompletableFuture<OrderWrite>> futureMap = new ConcurrentHashMap<>();

    public DynamicTopicListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topicPattern = "order-query-result-.*")
    public void handleResponse(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            OrderQueryResultEvent resultEvent = objectMapper.readValue(message, OrderQueryResultEvent.class);
            CompletableFuture<OrderWrite> future = futureMap.remove(topic);
            if (future != null) {
                future.complete(resultEvent.orderInfo());
            }
        } catch (Exception e) {
            log.error("응답 처리 중 오류 발생", e);
        }
    }

    public void registerFuture(String topic, CompletableFuture<OrderWrite> future) {
        futureMap.put(topic, future);
    }
}