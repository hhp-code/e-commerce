package com.ecommerce.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    LocalDateTime getOccurredOn();
    String getEventType();

    default String getEventName() {
        return this.getClass().getSimpleName();
    }
}
