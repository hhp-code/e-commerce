package com.ecommerce.infra.event.store;

import com.ecommerce.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventStore {
    private final List<DomainEvent> events = new ArrayList<>();

    public void save(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> getEvents() {
        return new ArrayList<>(events);
    }
}
