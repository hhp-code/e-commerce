package com.ecommerce.domain.outbox;

import java.util.List;

public interface OutboxRepository {
    void save(OutboxMessage message);
    List<OutboxMessage> findUnprocessedMessages();
    void delete(String id);
}
