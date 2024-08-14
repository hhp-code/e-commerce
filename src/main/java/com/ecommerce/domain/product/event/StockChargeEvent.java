package com.ecommerce.domain.product.event;

import com.ecommerce.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockChargeEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long productId,
        int quantityChange
) implements DomainEvent {
    public StockChargeEvent(Long productId, int quantityChange) {
        this(UUID.randomUUID(), LocalDateTime.now(), productId, quantityChange);
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
        return "StockRestore";
    }
}