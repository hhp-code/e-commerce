package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderItemAddEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long orderId,
        Long productId,
        int quantity
) implements DomainEvent {

    public OrderItemAddEvent(Long orderId, Long productId, int quantity) {
        this(UUID.randomUUID(), LocalDateTime.now(), orderId,productId,quantity);
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
        return "order-item-add";
    }

    @Override
    public String getAggregateId() {
        return orderId.toString();
    }
}
