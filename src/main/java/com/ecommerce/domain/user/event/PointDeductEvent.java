package com.ecommerce.domain.user.event;

import com.ecommerce.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PointDeductEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long userId,
        BigDecimal pointChange
) implements DomainEvent {
    public PointDeductEvent(Long userId, BigDecimal pointChange) {
        this(UUID.randomUUID(), LocalDateTime.now(), userId, pointChange);
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
        return "PointDeduct";
    }

    @Override
    public String getAggregateId() {
        return userId.toString();
    }
}