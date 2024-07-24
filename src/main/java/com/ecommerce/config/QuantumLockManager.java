package com.ecommerce.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;
import java.time.Duration;
import java.util.UUID;
@Component
public class QuantumLockManager {

    private final ConcurrentHashMap<String, QuantumLock> locks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public <T> T executeWithLock(String resourceId, Duration timeout, Supplier<T> action) throws TimeoutException {
        QuantumLock lock = locks.computeIfAbsent(resourceId, k -> new QuantumLock());
        String lockerId = UUID.randomUUID().toString();

        try {
            if (lock.acquire(lockerId, timeout)) {
                return action.get();
            } else {
                throw new TimeoutException("시간동안 락 획득에 실패했습니다.");
            }
        } finally {
            lock.release(lockerId);
        }
    }

    private class QuantumLock {
        private final AtomicReference<String> owner = new AtomicReference<>();
        private final ConcurrentLinkedQueue<String> waitQueue = new ConcurrentLinkedQueue<>();
        private final AtomicInteger waitCount = new AtomicInteger(0);

        public boolean acquire(String lockerId, Duration timeout) {
            waitQueue.offer(lockerId);
            waitCount.incrementAndGet();

            try {
                return CompletableFuture.supplyAsync(() -> {
                    while (true) {
                        if (owner.compareAndSet(null, lockerId)) {
                            waitQueue.remove(lockerId);
                            return true;
                        }
                        if (!waitQueue.peek().equals(lockerId)) {
                            LockSupport.parkNanos(ThreadLocalRandom.current().nextLong(1000, 1000000));
                        }
                    }
                }).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                waitQueue.remove(lockerId);
                return false;
            } finally {
                waitCount.decrementAndGet();
            }
        }

        public void release(String lockerId) {
            owner.compareAndSet(lockerId, null);
            if (waitCount.get() > 0) {
                scheduler.schedule(this::notifyWaiters, 0, TimeUnit.NANOSECONDS);
            }
        }

        private void notifyWaiters() {
            LockSupport.unpark(Thread.currentThread());
        }
    }
}