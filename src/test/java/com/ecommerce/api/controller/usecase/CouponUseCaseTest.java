package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserCouponService;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponUseCaseTest {
    @Mock
    private CouponService couponService;

    @Mock
    private UserService userService;
    @Mock
    private UserCouponService userCouponService;

    @Mock
    private OrderService orderService;

    private CouponUseCase couponUseCase;
    private CouponCommand.Issue issue;
    private User user;
    private Coupon coupon;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        couponUseCase = new CouponUseCase(orderService, couponService, userService,userCouponService);
        issue = new CouponCommand.Issue(1L, 1L, Instant.now());
        coupon = new Coupon(1L, "CODE123", BigDecimal.TEN, DiscountType.FIXED_AMOUNT, 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        user = new User(1L,"test", BigDecimal.TEN);
    }


    @Test
    @DisplayName("이미 사용된 쿠폰 사용 시도")
    void useCouponAlreadyUsed() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;


        // When & Then
        assertThrows(RuntimeException.class, () -> couponUseCase.useCoupon(userId, couponId));
    }

    @Test
    @DisplayName("사용자 찾기 실패")
    void issueCouponToUserUserNotFound() {
        // Given
        Long userId = 1L;
        when(userService.getUser(userId)).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> couponUseCase.issueCouponToUser(issue));
    }

    @Test
    @DisplayName("사용자에게 쿠폰 발급 성공")
    void issueCouponToUserSuccess() {
        // Given
        long couponId = 1L;


        when(userService.getUser(issue.userId())).thenReturn(user);
        when(couponService.deductCoupon(issue.couponId())).thenReturn(coupon);
        when(userCouponService.updateUserCoupon(user,coupon)).thenReturn(user);
        // When
        User result = couponUseCase.issueCouponToUser(issue);

        // Then
        assertNotNull(result);
        assertEquals(user, result);
        assertEquals(coupon, result.getCoupon(couponId));
    }
}
