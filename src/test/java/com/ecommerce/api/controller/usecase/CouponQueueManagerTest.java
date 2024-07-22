package com.ecommerce.api.controller.usecase;

import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class CouponQueueManagerTest {

    @Autowired
    private CouponQueueManager couponQueueManager;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserService userService;

    private static final int COUPON_COUNT = 5;
    private static final int USER_COUNT = 10;

    @BeforeEach
    void setUp() {
        createTestData();
    }

    private void createTestData() {
        Coupon coupon = new Coupon("TESTCOUPON", BigDecimal.valueOf(30), DiscountType.FIXED_AMOUNT, COUPON_COUNT);
        couponService.save(coupon);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < USER_COUNT; i++) {
            users.add(new User("user" + i + "@example.com", BigDecimal.valueOf(1000)));
        }
        userService.saveAll(users);
    }


    @Test
    void testCouponIssuedCorrectly() {
        for (int i = 0; i < USER_COUNT; i++) {
            final long userId = i + 1;
            final long couponId = 1L;

            couponQueueManager.addToQueueAsync(new CouponCommand.Issue(couponId, userId, CouponCommand.Issue.Status.PENDING, Instant.now()));
        }

        long successCount =0;
        long failCount =0;
        for (int i = 0; i < USER_COUNT; i++) {
            CouponCommand.Issue poll = couponQueueManager.getCouponQueue().poll();
            try {
                couponQueueManager.processCouponRequest(poll);
                CouponCommand.Issue issue = couponQueueManager.checkStatus(poll.userId());
                if (issue != null && !issue.status().equals(CouponCommand.Issue.Status.FAILED)) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                if (e.getCause() instanceof IllegalStateException) {
                    failCount++;
                } else {
                    throw e;
                }
            }
        }

        long finalSuccessCount = successCount;
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            long completedCount = couponQueueManager.getResultMap().values().stream()
                    .filter(issue -> issue.status().equals(CouponCommand.Issue.Status.COMPLETED))
                    .count();
            return completedCount == finalSuccessCount;
        });

        System.out.println("Success count: " + successCount);
        assertEquals(successCount, couponQueueManager.getResultMap().values().stream()
                .filter(issue -> issue.status().equals(CouponCommand.Issue.Status.COMPLETED))
                .count());
        System.out.println("Fail count: " + failCount);
        assertEquals(failCount, couponQueueManager.getResultMap().values().stream()
                .filter(issue -> issue.status().equals(CouponCommand.Issue.Status.FAILED))
                .count());
    }
}
