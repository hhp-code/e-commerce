package com.ecommerce.domain.order.event;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderCreateEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        Long userId,
        List<OrderItemWrite> orderItems
) implements DomainEvent {

    public OrderCreateEvent(Long userId, List<OrderItemWrite> orderItems) {
        this(UUID.randomUUID(), LocalDateTime.now(), userId, orderItems);
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
        return "order-create";
    }
}
