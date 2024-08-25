package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.order.OrderWrite;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderQueryResultEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long orderId,
        OrderWrite orderInfo
) implements DomainEvent {

    public OrderQueryResultEvent(Long orderId, OrderWrite orderInfo) {
        this(UUID.randomUUID(), LocalDateTime.now(), orderId, orderInfo);
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
        return "order-query-result";
    }

    @Override
    public String getAggregateId() {
        return orderId.toString();
    }
}
