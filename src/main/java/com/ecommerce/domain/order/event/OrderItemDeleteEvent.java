package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderItemDeleteEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long orderId,
        Long productId
) implements DomainEvent {

    public OrderItemDeleteEvent(Long orderId, Long productId) {
        this(UUID.randomUUID(), LocalDateTime.now(), orderId,productId);
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
        return "order-item-delete";
    }

    @Override
    public String getAggregateId() {
        return orderId.toString();
    }
}
