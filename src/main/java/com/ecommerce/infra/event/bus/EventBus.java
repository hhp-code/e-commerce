package com.ecommerce.infra.event.bus;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventBus implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventBus(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
