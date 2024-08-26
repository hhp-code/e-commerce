package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.User;
import com.ecommerce.interfaces.scheduler.CouponQueueManager;
import com.ecommerce.application.usecase.CouponUseCase;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Disabled("아직 완성되지 않았습니다.")
@ExtendWith(MockitoExtension.class)
class CouponQueueManagerTest {

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private CouponUseCase couponUseCase;


    @Mock
    private CouponService couponService;

    private CouponQueueManager couponQueueManager;

    @Mock
    private QuantumLockManager quantumLockManager;

    private CouponCommand.Issue testIssue;
    private User testUser;

    @BeforeEach
    void setUp() {
        CouponWrite testCoupon = new CouponWrite("TEST123", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 10);
        testIssue =new CouponCommand.Issue(1L, 1L, Instant.now());
        testUser = new User("test", null, List.of(testCoupon));

        couponQueueManager = new CouponQueueManager(couponUseCase, couponService,quantumLockManager);
    }



}