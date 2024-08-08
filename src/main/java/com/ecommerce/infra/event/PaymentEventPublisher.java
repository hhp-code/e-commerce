package com.ecommerce.infra.event;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
