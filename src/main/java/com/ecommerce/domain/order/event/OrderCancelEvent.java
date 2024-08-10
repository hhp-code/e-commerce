package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCancelEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long orderId
) implements DomainEvent {

    public OrderCancelEvent(
            UUID eventId,
            LocalDateTime occurredOn,
            Long orderId
    ) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.orderId = orderId;
    }

    public OrderCancelEvent(Long orderId) {
        this(UUID.randomUUID(), LocalDateTime.now(), orderId);
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
        return this.getClass().getSimpleName();
    }

    // 카프카 메시지 키로 사용할 메서드
    public String getKey() {
        return orderId.toString();
    }
}