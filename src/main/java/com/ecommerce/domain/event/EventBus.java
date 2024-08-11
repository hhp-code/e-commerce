package com.ecommerce.domain.event;

import java.util.function.Consumer;

public interface EventBus {
    void publish(String topic, String event);
    void subscribe(String topic, Consumer<String> eventHandler);
}
