package com.ecommerce.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
public class QuantumLockManager {

    private final ConcurrentHashMap<String, QuantumLock> locks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "quantum-lock-pool-" + threadNumber.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            }
    );

    public <T> T executeWithLock(String resourceId, Duration timeout, Supplier<T> action) throws TimeoutException, InterruptedException {
        QuantumLock lock = locks.computeIfAbsent(resourceId, k -> new QuantumLock());
        String lockerId = UUID.randomUUID().toString();

        if (!lock.acquire(lockerId, timeout)) {
            throw new TimeoutException("시간동안 락 획득에 실패했습니다.");
        }

        try {
            return action.get();
        } finally {
            lock.release(lockerId);
        }
    }

    private class QuantumLock {
        private final ReentrantLock lock = new ReentrantLock(true);
        private final AtomicBoolean isLocked = new AtomicBoolean(false);
        private volatile String currentOwner = null;

        public boolean acquire(String lockerId, Duration timeout) throws InterruptedException {
            long endTime = System.currentTimeMillis() + timeout.toMillis();
            while (System.currentTimeMillis() < endTime) {
                if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                    try {
                        if (!isLocked.get()) {
                            isLocked.set(true);
                            currentOwner = lockerId;
                            return true;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
            return false;
        }

        public void release(String lockerId) {
            lock.lock();
            try {
                if (lockerId.equals(currentOwner)) {
                    isLocked.set(false);
                    currentOwner = null;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    // 리소스 정리를 위한 메서드
    public void cleanup() {
        locks.entrySet().removeIf(entry -> !entry.getValue().isLocked.get());
    }

    // 주기적으로 cleanup 메서드를 호출하는 스케줄러 설정
    @PostConstruct
    public void scheduleCleanup() {
        scheduler.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.HOURS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}