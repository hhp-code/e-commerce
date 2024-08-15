package com.ecommerce.infra.outbox;

import com.ecommerce.domain.outbox.OutboxMessage;
import com.ecommerce.domain.outbox.OutboxRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class RedisOutboxRepository implements OutboxRepository {
    private final RedisTemplate<String, OutboxMessage> redisTemplate;
    private static final String OUTBOX_KEY = "outbox:messages";

    public RedisOutboxRepository(RedisTemplate<String, OutboxMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(OutboxMessage message) {
        redisTemplate.opsForList().rightPush(OUTBOX_KEY, message);
    }

    @Override
    public List<OutboxMessage> findUnprocessedMessages() {
        return redisTemplate.opsForList().range(OUTBOX_KEY, 0, -1);
    }

    @Override
    public void delete(String id) {
        redisTemplate.opsForList().remove(OUTBOX_KEY, 1, id);
    }
}