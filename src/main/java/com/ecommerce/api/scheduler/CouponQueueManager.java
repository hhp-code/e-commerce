package com.ecommerce.api.scheduler;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CouponQueueManager {

    private final ConcurrentLinkedQueue<CouponCommand.Issue> couponQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, CouponCommand.Issue> resultMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicInteger processedRequestsCount = new AtomicInteger(0);

    private final CouponUseCase couponUseCase;
    private final UserService userService;
    private final CouponService couponService;

    @Getter @Setter
    private Long currentCouponId;

    @Getter @Setter
    private int rateLimit = 1000; // 초당 처리할 수 있는 최대 요청 수

    public CouponQueueManager(CouponUseCase couponUseCase, UserService userService, CouponService couponService) {
        this.couponUseCase = couponUseCase;
        this.userService = userService;
        this.couponService = couponService;
    }

    @Scheduled(fixedRate = 1000)
    public void processCouponRequests() {
        if (currentCouponId == null) {
            log.warn("No coupon ID set for processing");
            return;
        }

        int remainingCoupons = couponService.getRemainingQunatity(currentCouponId);
        if (remainingCoupons <= 0) {
            log.info("No more coupons available. Stopping processing.");
            return;
        }

        int processLimit = Math.min(Math.min(remainingCoupons, couponQueue.size()), rateLimit);
        processedRequestsCount.set(0);
        processRequests(processLimit);

        log.info("Processed {} requests. Remaining coupons: {}", processedRequestsCount.get(), remainingCoupons - processedRequestsCount.get());
    }

    private void processRequests(int limit) {
        for (int i = 0; i < limit; i++) {
            CouponCommand.Issue request = couponQueue.poll();
            if (request != null) {
                executorService.submit(() -> processCouponRequest(request));
            } else {
                break;
            }
        }
    }

    private void processCouponRequest(CouponCommand.Issue request) {
        try {
            log.info("Processing coupon request for user: {}", request.userId());
            updateRequestStatus(request, CouponCommand.Issue.Status.PROCESSING);
            couponUseCase.issueCouponToUser(request);
            updateRequestStatus(request, CouponCommand.Issue.Status.COMPLETED);
            log.info("Coupon request completed for user: {}", request.userId());
        } catch (Exception e) {
            log.error("Failed to process coupon request for user: {}", request.userId(), e);
            updateRequestStatus(request, CouponCommand.Issue.Status.FAILED);
        } finally {
            processedRequestsCount.incrementAndGet();
        }
    }

    private void updateRequestStatus(CouponCommand.Issue request, CouponCommand.Issue.Status status) {
        CouponCommand.Issue updatedRequest = new CouponCommand.Issue(request.userId(), request.couponId(), status, Instant.now());
        resultMap.put(request.userId(), updatedRequest);
    }

    public CompletableFuture<User> addToQueueAsync(CouponCommand.Issue issue) {
        CouponCommand.Issue pending = new CouponCommand.Issue(issue.couponId(), issue.userId(), CouponCommand.Issue.Status.PENDING, issue.timeStamp());
        resultMap.put(issue.userId(), pending);
        couponQueue.offer(pending);
        return CompletableFuture.supplyAsync(() -> waitForCompletion(issue), executorService);
    }

    private User waitForCompletion(CouponCommand.Issue issue) {
        long startTime = System.currentTimeMillis();
        long timeout = 60000; // 60 seconds timeout

        while (System.currentTimeMillis() - startTime < timeout) {
            CouponCommand.Issue current = resultMap.get(issue.userId());
            if (current.status() == CouponCommand.Issue.Status.COMPLETED) {
                return userService.getUser(issue.userId());
            } else if (current.status() == CouponCommand.Issue.Status.FAILED) {
                throw new RuntimeException("Coupon issue failed");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for coupon issue", e);
            }
        }
        throw new RuntimeException("Coupon issue timed out");
    }

    public CouponCommand.Issue checkStatus(Long userId) {
        return resultMap.getOrDefault(userId, new CouponCommand.Issue(null, userId, CouponCommand.Issue.Status.PENDING, Instant.now()));
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}