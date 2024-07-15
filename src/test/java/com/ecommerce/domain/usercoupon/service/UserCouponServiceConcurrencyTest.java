package com.ecommerce.domain.usercoupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.service.repository.UserCouponRepository;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserCouponServiceConcurrencyTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUseCase couponUseCase;

    private static final int THREAD_COUNT = 100;
    private static final int TIMEOUT_SECONDS = 10;
    private final CouponCommand.Issue issue = new CouponCommand.Issue(1L, 1L);

    @BeforeEach
    public void setup() {
        User user = new User(1L, "TestUser", BigDecimal.ZERO);
        userService.saveUser(user);

        CouponCommand.Create create = new CouponCommand.Create("TestCoupon",
                BigDecimal.TEN, THREAD_COUNT, DiscountType.FIXED_AMOUNT, LocalDateTime.now(), LocalDateTime.now().plusDays(7), true);
        couponService.createCoupon(create);
    }

    @Test
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void testConcurrentCouponIssuance() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    couponUseCase.issueCouponToUser(issue);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "Timeout occurred while waiting for threads to complete");
        assertEquals(THREAD_COUNT, successCount.get() + failCount.get(), "Total operations should match thread count");

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Coupon> issuedCoupons = userCouponRepository.getAllCouponsByUserId(1L);
            assertEquals(1, issuedCoupons.size(), "Only one coupon should be issued");
        });

        System.out.println("Successful issuances: " + successCount.get());
        System.out.println("Failed issuances: " + failCount.get());
    }

    @Test
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void testCouponLimitIssuance() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    couponUseCase.issueCouponToUser(issue);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "Timeout occurred while waiting for threads to complete");

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Coupon> issuedCoupons = userCouponRepository.getAllCouponsByUserId(1L);
            assertTrue(issuedCoupons.size() <= THREAD_COUNT, "Number of issued coupons should not exceed the limit");
            assertEquals(THREAD_COUNT, successCount.get() + failCount.get(), "Total operations should match thread count");
        });

        System.out.println("Successful issuances: " + successCount.get());
        System.out.println("Failed issuances: " + failCount.get());
    }

    @Test
    @Transactional(readOnly = true)
    public void testConcurrentCouponRetrieval() throws InterruptedException {
        // 먼저 쿠폰을 발급
        couponUseCase.issueCouponToUser(issue);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    List<Coupon> userCoupons = userService.getUserCoupons(1L);
                    if (!userCoupons.isEmpty()) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // 예외 처리
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "Timeout occurred while waiting for threads to complete");
        assertEquals(THREAD_COUNT, successCount.get(), "All threads should successfully retrieve the coupon");
    }
}