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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
@Slf4j
@Component
public class CouponQueueManager {

    @Getter
    private final ConcurrentLinkedQueue<CouponCommand.Issue> couponQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Getter
    private final Map<Long, CouponCommand.Issue> resultMap = new ConcurrentHashMap<>();
    private ScheduledFuture<?> scheduledFuture;


    private final CouponUseCase couponUsecase;
    private final UserService userService;
    private final CouponService couponService;
    private long currentRate = 1000;

    @Setter
    private Long currentCouponId ;



    public CouponQueueManager(CouponUseCase couponUsecase, UserService userService, CouponService couponService) {
        this.couponUsecase = couponUsecase;
        this.userService = userService;
        this.couponService = couponService;
    }

    public void stopProcessing() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
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
            stopProcessing();
            return;
        }
        int queueSize = couponQueue.size();
        int processLimit = Math.min(remainingCoupons, queueSize);

        for (int i = 0; i < processLimit; i++) {
            CouponCommand.Issue request = couponQueue.poll();
            if (request != null) {
                processCouponRequest(request);
            } else {
                break;
            }
        }

        log.info("Processed {} requests. Remaining coupons: {}", processLimit, remainingCoupons - processLimit);
    }


    public void processCouponRequest(CouponCommand.Issue request) {
        try {
            log.info("Processing coupon request for user: {}", request.userId());
            updateRequestStatus(request, CouponCommand.Issue.Status.PROCESSING);
            couponUsecase.issueCouponToUser(request);
            updateRequestStatus(request, CouponCommand.Issue.Status.COMPLETED);
            log.info("Coupon request completed for user: {}", request.userId());
        } catch (Exception e) {
            log.error("Failed to process coupon request for user: {}", request.userId(), e);
            updateRequestStatus(request, CouponCommand.Issue.Status.FAILED);
        }
    }

    private CouponCommand.Issue updateRequestStatus(CouponCommand.Issue request, CouponCommand.Issue.Status status) {
        CouponCommand.Issue updatedRequest = new CouponCommand.Issue(request.userId(), request.couponId(), status, Instant.now());
        resultMap.put(request.userId(), updatedRequest);
        return updatedRequest;
    }

    private void adjustScheduleRate(long newRate) {
        if (newRate != currentRate) {
            currentRate = newRate;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            scheduledFuture = scheduler.scheduleAtFixedRate(this::processCouponRequests, 0, currentRate, TimeUnit.MILLISECONDS);
        }
    }

    public CompletableFuture<User> addToQueueAsync(CouponCommand.Issue issue) {
        CouponCommand.Issue pending = new CouponCommand.Issue(issue.couponId(), issue.userId(), CouponCommand.Issue.Status.PENDING, issue.timeStamp());
        resultMap.put(issue.userId(), pending);
        couponQueue.offer(pending);
        return CompletableFuture.supplyAsync(() -> {
            while (true) {
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
                }
            }
        });
    }





    public CouponCommand.Issue checkStatus( Long userId) {
        return resultMap.getOrDefault(userId, new CouponCommand.Issue(null, userId, CouponCommand.Issue.Status.PENDING, Instant.now()));
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