package com.ecommerce.api.controller.usecase;

import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CouponQueueManagerTest {

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private CouponUseCase couponUseCase;


    @Mock
    private CouponService couponService;

    private CouponQueueManager couponQueueManager;

    private CouponCommand.Issue testIssue;
    private User testUser;

    @Mock
    private QuantumLockManager quantumLockManager;
    @BeforeEach
    void setUp() {
        Coupon testCoupon = new Coupon("TEST123", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 10);
        testIssue =new CouponCommand.Issue(1L, 1L, Instant.now());
        testUser = new User(1L, "test", null, List.of(testCoupon));

        couponQueueManager = new CouponQueueManager(couponUseCase, couponService,quantumLockManager);
    }



}