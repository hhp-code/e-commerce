package com.ecommerce.api.controller.usecase;

import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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



}
