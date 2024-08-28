package com.ecommerce.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Slf4j
@Component
public class RedisLockManager {

    private final RedissonClient redissonClient;
    private final ExecutorService executorService;

    public RedisLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.executorService = Executors.newCachedThreadPool();
    }

    public <T> T executeWithLock(String resourceId, Duration lockTimeout, Duration actionTimeout, Supplier<T> action) throws InterruptedException, TimeoutException, ExecutionException {
        RLock lock = redissonClient.getLock("lock:" + resourceId);
        boolean locked = false;

        try {
            locked = lock.tryLock(lockTimeout.toMillis(), TimeUnit.MILLISECONDS);
            if (locked) {
                log.debug("Lock acquired for resource: {}", resourceId);

                Future<T> future = executorService.submit(action::get);
                try {
                    return future.get(actionTimeout.toMillis(), TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    throw new TimeoutException("Action execution timed out for resource: " + resourceId);
                }
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

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}