package com.ecommerce.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
