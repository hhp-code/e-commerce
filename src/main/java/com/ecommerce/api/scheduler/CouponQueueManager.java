package com.ecommerce.api.scheduler;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.domain.coupon.Coupon;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CouponQueueManager {

    @Getter
    private final ConcurrentLinkedQueue<CouponCommand.Issue> couponQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final Map<Long, CouponCommand.Issue> resultMap = new ConcurrentHashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicInteger processedRequestsCount = new AtomicInteger(0);

    private final CouponUseCase couponUseCase;
    private final UserService userService;
    private final CouponService couponService;

    @Getter @Setter
    private Long currentCouponId;

    @Getter @Setter
    private int rateLimit = 1000;

    public CouponQueueManager(CouponUseCase couponUseCase, UserService userService, CouponService couponService) {
        this.couponUseCase = couponUseCase;
        this.userService = userService;
        this.couponService = couponService;
    }
    public boolean shouldProcessCoupons() {
        return currentCouponId != null;
    }
    @Scheduled(fixedRate = 1000)
    public void processCouponRequests() {
        if(!shouldProcessCoupons()){
            return;
        }
        Coupon coupon = couponService.getCoupon(currentCouponId);
        int quantity = coupon.getQuantity();
        if (quantity <= 0) {
            log.info("쿠폰이 더 없습니다잉");
            return;
        }

        int processLimit = Math.min(Math.min(quantity, couponQueue.size()), rateLimit);
        processedRequestsCount.set(0);
        processRequests(processLimit);

        log.info("처리한 {} 요청. 남은 쿠폰: {}", processedRequestsCount.get(), quantity - processedRequestsCount.get());
    }

    private void processRequests(int limit) {
        List<CouponCommand.Issue> batch = new ArrayList<>();
        for (int i = 0; i < limit && !couponQueue.isEmpty(); i++) {
            CouponCommand.Issue request = couponQueue.poll();
            if (request != null) {
                batch.add(request);
            }
        }

        if (!batch.isEmpty()) {
            batch.sort(Comparator.comparing(CouponCommand.Issue::timeStamp));
            executorService.submit(() -> processBatch(batch));
        }
    }

    private void processBatch(List<CouponCommand.Issue> batch) {
        for (CouponCommand.Issue request : batch) {
            processCouponRequest(request);
        }
    }

    public void processCouponRequest(CouponCommand.Issue request) {
        try {
            log.info("사용자 쿠폰 처리 요청중: {}", request.userId());
            updateRequestStatus(request, CouponCommand.Issue.Status.PROCESSING);
            couponUseCase.issueCouponToUser(request);
            updateRequestStatus(request, CouponCommand.Issue.Status.COMPLETED);
            log.info("사용자 쿠폰 처리 완료: {}", request.userId());
        } catch (Exception e) {
            log.error("사용자 쿠폰 처리 실패: {}", request.userId(), e);
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
        long timeout = 60000;

        while (System.currentTimeMillis() - startTime < timeout) {
            CouponCommand.Issue current = resultMap.get(issue.userId());
            if (current.status() == CouponCommand.Issue.Status.COMPLETED) {
                return userService.getUser(issue.userId());
            } else if (current.status() == CouponCommand.Issue.Status.FAILED) {
                throw new RuntimeException("쿠폰 발급 실패");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("쿠폰 발급중 오류가 발생했습니다.", e);
            }
        }
        throw new RuntimeException("쿠폰 발급에 시간이 초과되었습니다.");
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