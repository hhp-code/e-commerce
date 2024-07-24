package com.ecommerce.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CustomLockManager {

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final TransactionTemplate transactionTemplate;

    public CustomLockManager(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public <T> T executeWithLock(String lockKey, LockCallback<T> callback) {
        ReentrantLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock());
        lock.lock();
        try {
            return transactionTemplate.execute(status -> {
                try {
                    return callback.doInLock();
                } catch (Exception e) {
                    throw new RuntimeException("트랜잭션 실행 중 오류 발생", e);
                }
            });
        } finally {
            lock.unlock();
        }
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T doInLock() throws Exception;
    }
}
