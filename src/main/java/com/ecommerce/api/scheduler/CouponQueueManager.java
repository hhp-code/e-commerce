package com.ecommerce.api.scheduler;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CouponQueueManager {
    private static final int DEFAULT_RATE_LIMIT = 1000;
    private static final long SHUTDOWN_TIMEOUT = 60L;

    @Getter
    private final ConcurrentLinkedQueue<CouponCommand.Issue> couponQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, CompletableFuture<User>> userFutureMap = new ConcurrentHashMap<>();

    private final ExecutorService executorService;
    private final AtomicInteger processedRequestsCount = new AtomicInteger(0);

    private final TransactionTemplate transactionTemplate;
    private final CouponUseCase couponUseCase;
    private final CouponService couponService;

    @Getter
    private volatile Long currentCouponId;

    public CouponQueueManager(TransactionTemplate transactionTemplate, CouponUseCase couponUseCase,
                              CouponService couponService) {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.transactionTemplate = transactionTemplate;
        this.couponUseCase = couponUseCase;
        this.couponService = couponService;
        scheduleCleanup();
    }

    public CompletableFuture<User> addToQueueAsync(CouponCommand.Issue issue) {
        this.currentCouponId = issue.couponId();
        couponQueue.offer(issue);

        CompletableFuture<User> future = new CompletableFuture<>();
        userFutureMap.put(issue.userId(), future);

        return future.thenApplyAsync(this::logUserCouponInfo, executorService)
                .exceptionally(e -> {
                    log.error("사용자 {}에게 쿠폰 발급 실패", issue.userId(), e);
                    throw new CompletionException(e);
                });
    }


    @Scheduled(fixedRate = 1000)
    public void processCouponRequests() {
        if (currentCouponId == null) return;

        int remainingCoupons = couponService.getRemainingQuantity(currentCouponId);
        if (remainingCoupons <= 0) {
            log.info("쿠폰이 모두 소진되었습니다. 처리를 중단합니다.");
            return;
        }
        int processLimit = Math.min(Math.min(remainingCoupons, couponQueue.size()), DEFAULT_RATE_LIMIT);
        processedRequestsCount.set(0);

        for (int i = 0; i < processLimit; i++) {
            CouponCommand.Issue request = couponQueue.poll();
            if (request == null) break;
            CompletableFuture.runAsync(() -> processCouponRequest(request), executorService);
        }
        log.info("처리된 요청: {}. 남은 쿠폰: {}", processedRequestsCount.get(), remainingCoupons - processedRequestsCount.get());

    }

    private void processCouponRequest(CouponCommand.Issue request) {
        MDC.put("userId", String.valueOf(request.userId()));
        MDC.put("couponId", String.valueOf(request.couponId()));
        try {
            log.info("쿠폰 요청 처리 중");
            User user =  transactionTemplate.execute(status -> couponUseCase.issueCouponToUser(request));
            CompletableFuture<User> future = userFutureMap.remove(request.userId());
            if (future != null) {
                future.complete(user);
            }
            log.info("쿠폰 요청 처리 완료");
        } catch (Exception e) {
            log.error("쿠폰 요청 처리 중 오류 발생", e);
        } finally {
            processedRequestsCount.incrementAndGet();
            MDC.clear();
        }
    }
    private void scheduleCleanup() {
        long cleanupInterval = 30000L;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                this::cleanupCompletedFutures,
                cleanupInterval,
                cleanupInterval,
                TimeUnit.MILLISECONDS
        );
    }

    private void cleanupCompletedFutures() {
        userFutureMap.entrySet().removeIf(entry -> entry.getValue().isDone());
    }


    private User logUserCouponInfo(User user) {
        if (!user.getCoupons().isEmpty()) {
            log.info("사용자 {}에게 쿠폰 발급 성공, 쿠폰 수: {}, 첫 번째 쿠폰 코드: {}",
                    user.getId(), user.getCoupons().size(),
                    user.getCoupons().getFirst().getCode());
        } else {
            log.info("사용자 {}에게 쿠폰 발급 성공했지만, 쿠폰이 없습니다.", user.getId());
        }
        return user;
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