package com.ecommerce.api.scheduler;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class CouponQueueManager {
    private static final long SHUTDOWN_TIMEOUT = 60L;

    @Getter
    private final Map<Long, CompletableFuture<User>> userFutureMap = new ConcurrentHashMap<>();

    private final ExecutorService executorService;
    private final PriorityBlockingQueue<CouponCommand.Issue> couponQueue;

    private final CouponUseCase couponUseCase;
    private final CouponService couponService;

    @Getter
    private final AtomicReference<Long> currentCouponId = new AtomicReference<>();

    private final ReentrantLock couponProcessLock = new ReentrantLock();
    private final QuantumLockManager quantumLockManager;

    public CouponQueueManager(CouponUseCase couponUseCase, CouponService couponService, QuantumLockManager quantumLockManager) {
        this.quantumLockManager = quantumLockManager;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.couponQueue = new PriorityBlockingQueue<>(11, Comparator.comparing(CouponCommand.Issue::issuedAt));
        this.couponUseCase = couponUseCase;
        this.couponService = couponService;
        startProcessingQueue();
    }

    public User addToQueueAsync(CouponCommand.Issue issue) {
        currentCouponId.set(issue.couponId());
        CompletableFuture<User> future = new CompletableFuture<>();
        userFutureMap.put(issue.userId(), future);
        couponQueue.offer(issue);
        return future.join();
    }

    private void startProcessingQueue() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CouponCommand.Issue issue = couponQueue.take();
                    User user = processCouponRequest(issue);
                    CompletableFuture<User> future = userFutureMap.remove(issue.userId());
                    if (future != null) {
                        future.complete(user);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("쿠폰 처리 중 오류 발생", e);
                }
            }
        });
    }

    private User processCouponRequest(CouponCommand.Issue issue) {
        String lockKey = "coupon:" + issue.couponId();
        Duration timeout = Duration.ofSeconds(5);
        try{
            return quantumLockManager.executeWithLock(lockKey,timeout, () -> {
                int remainingCoupons = couponService.getStock(issue.couponId());
                log.info("남은 쿠폰 수량: {}", remainingCoupons);
                if (remainingCoupons <= 0) {
                    throw new RuntimeException("쿠폰이 모두 소진되었습니다.");
                }
                return couponUseCase.issueCouponToUser(issue);
        }
        );
        } catch (Exception e) {
            throw new RuntimeException("쿠폰 발급에 실패했습니다.", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
