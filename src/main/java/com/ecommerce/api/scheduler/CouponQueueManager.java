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
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CouponQueueManager {
    private final Semaphore semaphore = new Semaphore(0);
    @Getter
    private final ConcurrentLinkedQueue<CouponCommand.Issue> couponQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final Map<Long, CouponCommand.Issue> resultMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicInteger processedRequestsCount = new AtomicInteger(0);

    private final TransactionTemplate transactionTemplate;
    private final CouponUseCase couponUseCase;
    private final UserService userService;
    private final CouponService couponService;

    @Getter
    private Long currentCouponId;

    @Getter
    @Setter
    private int rateLimit = 1000;

    public CouponQueueManager(TransactionTemplate transactionTemplate, CouponUseCase couponUseCase,
                              UserService userService, CouponService couponService) {
        this.transactionTemplate = transactionTemplate;
        this.transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_REPEATABLE_READ);
        this.couponUseCase = couponUseCase;
        this.userService = userService;
        this.couponService = couponService;
    }

    @Scheduled(fixedRate = 1000)
    public void processCouponRequests() {
        if (currentCouponId == null) return;

        int remainingCoupons = couponService.getRemainingQuantity(currentCouponId);
        if (remainingCoupons <= 0) {
            log.info("쿠폰이 모두 소진되었습니다. 처리를 중단합니다.");
            return;
        }

        int processLimit = Math.min(Math.min(remainingCoupons, couponQueue.size()), rateLimit);
        processedRequestsCount.set(0);
        processRequests(processLimit);
        log.info("처리된 요청: {}. 남은 쿠폰: {}", processedRequestsCount.get(), remainingCoupons - processedRequestsCount.get());
    }

    private void processRequests(int limit) {
        for (int i = 0; i < limit; i++) {
            CouponCommand.Issue request = couponQueue.poll();
            if (request != null) {
                CompletableFuture.runAsync(() -> processCouponRequest(request), executorService)
                        .exceptionally(e -> {
                            log.error("쿠폰 요청 처리 중 오류 발생", e);
                            return null;
                        });
            } else {
                break;
            }
        }
    }
    private User processedUser;

    public void processCouponRequest(CouponCommand.Issue request) {
        MDC.put("userId", String.valueOf(request.userId()));
        MDC.put("couponId", String.valueOf(request.couponId()));
        try {
            log.info("쿠폰 요청 처리 중");
            updateRequestStatus(request, CouponCommand.Issue.Status.PROCESSING);

            transactionTemplate.execute(status -> {
                User processedUser = couponUseCase.issueCouponToUser(request);
                log.info(processedUser.getCoupons().size() + "wowwow");
                this.processedUser = processedUser;
                return processedUser;
            });
            semaphore.release();

            updateRequestStatus(request, CouponCommand.Issue.Status.COMPLETED);
            log.info("쿠폰 요청 처리 완료");
        } catch (Exception e) {
            log.error("쿠폰 요청 처리 실패", e);
            updateRequestStatus(request, CouponCommand.Issue.Status.FAILED);
        } finally {
            processedRequestsCount.incrementAndGet();
            MDC.clear();
        }
    }

    private void updateRequestStatus(CouponCommand.Issue request, CouponCommand.Issue.Status status) {
        CouponCommand.Issue updatedRequest = new CouponCommand.Issue(request.userId(), request.couponId(), status, Instant.now());
        resultMap.put(request.userId(), updatedRequest);
    }

    public CompletableFuture<User> addToQueueAsync(CouponCommand.Issue issue) {
        this.currentCouponId = issue.couponId();
        CouponCommand.Issue pending = new CouponCommand.Issue(issue.couponId(), issue.userId(), CouponCommand.Issue.Status.PENDING, issue.timeStamp());
        resultMap.put(issue.userId(), pending);
        couponQueue.offer(pending);

        return CompletableFuture.supplyAsync(() -> waitForCompletion(issue), executorService)
                .thenApplyAsync(user -> {
                    if (!user.getCoupons().isEmpty()) {
                        log.info("사용자 {}에게 쿠폰 발급 성공, 쿠폰 수: {}, 첫 번째 쿠폰 코드: {}",
                                user.getId(), user.getCoupons().size(),
                                user.getCoupons().getFirst().getCode());
                    } else {
                        log.info("사용자 {}에게 쿠폰 발급 성공했지만, 쿠폰이 없습니다.", user.getId());
                    }
                    return user;
                }, executorService)
                .exceptionally(e -> {
                    log.error("사용자 {}에게 쿠폰 발급 실패", issue.userId(), e);
                    throw new CompletionException(e);
                });
    }

    private User waitForCompletion(CouponCommand.Issue issue) {
        long startTime = System.currentTimeMillis();
        long timeout = 60000; // 60초 타임아웃
        while (System.currentTimeMillis() - startTime < timeout) {
            User user = transactionTemplate.execute(status -> {
                CouponCommand.Issue current = resultMap.get(issue.userId());
                if (current.status() == CouponCommand.Issue.Status.COMPLETED) {
                    try {
                        if(semaphore.tryAcquire(60,TimeUnit.SECONDS)){
                            return processedUser;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return userService.getUserWithCoupon(issue.userId());
                } else if (current.status() == CouponCommand.Issue.Status.FAILED) {
                    throw new RuntimeException("쿠폰 발급 실패");
                }
                return null;
            });
            if (user != null) {
                return user;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("쿠폰 발급 대기 중 인터럽트 발생", e);
            }
        }
        throw new RuntimeException("쿠폰 발급 시간 초과");
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