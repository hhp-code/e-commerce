package com.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
public class RedisLockManager {

    private final RedissonClient redissonClient;

    public RedisLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> T executeWithLock(String resourceId, Duration timeout, Supplier<T> action) throws InterruptedException {
        RLock lock = redissonClient.getLock("lock:" + resourceId);
        boolean locked = false;

        try {
            locked = lock.tryLock(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (locked) {
                log.debug("Lock acquired for resource: {}", resourceId);
                return action.get();
            } else {
                log.warn("Failed to acquire lock for resource: {}", resourceId);
                throw new RuntimeException("Failed to acquire lock for resource: " + resourceId);
            }
        } finally {
            if (locked) {
                lock.unlock();
                log.debug("Lock released for resource: {}", resourceId);
            }
        }
    }
}