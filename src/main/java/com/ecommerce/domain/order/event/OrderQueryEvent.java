package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderQueryEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long orderId,
        String callbackTopic
) implements DomainEvent {

    public OrderQueryEvent(Long orderId, String callbackTopic) {
        this(UUID.randomUUID(), LocalDateTime.now(), orderId, callbackTopic);
    }


    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "order-query";
    }

    @Override
    public String getAggregateId() {
        return orderId.toString();
    }

    public String getCallbackTopic() {
        return callbackTopic;
    }

}
