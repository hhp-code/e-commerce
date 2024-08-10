package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCancelEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long orderId
) implements DomainEvent {

    public OrderCancelEvent (Long orderId) {
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
        return "Cancel";
    }
}
