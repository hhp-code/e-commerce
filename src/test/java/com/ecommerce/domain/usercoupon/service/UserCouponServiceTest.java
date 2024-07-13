package com.ecommerce.domain.usercoupon.service;

import com.ecommerce.api.controller.domain.usercoupon.dto.UserCouponDto;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.service.repository.UserCouponRepository;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.usercoupon.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {
    @Mock
    private UserService userRepository;
    @Mock
    private CouponService couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private UserCouponService userCouponService;
    @Nested
    @DisplayName("issueCouponToUser 메서드")
    class IssueCouponToUserTest {
        @Test
        @DisplayName("사용자에게 쿠폰 발급 성공")
        void issueCouponToUserSuccess() {
            // Given
            Long userId = 1L;
            Long couponId = 1L;
            User user = new User("test", BigDecimal.ZERO);
            Coupon coupon = new Coupon();
            UserCoupon userCoupon = new UserCoupon(user, coupon);
            UserCouponCommand.UserCouponCreate command = new UserCouponCommand.UserCouponCreate(userId, new UserCouponDto.UserCouponRequest(couponId));

            when(userRepository.getUser(userId)).thenReturn(user);
            when(couponRepository.getCoupon(couponId)).thenReturn(coupon);
            when(userCouponRepository.getCouponByUser(user, coupon)).thenReturn(Optional.of(userCoupon));

            // When
            UserCoupon result = userCouponService.issueCouponToUser(command);

            // Then
            assertNotNull(result);
            assertEquals(user, result.getUser());
            assertEquals(coupon, result.getCoupon());
        }

        @Test
        @DisplayName("사용자 찾기 실패")
        void issueCouponToUserUserNotFound() {
            // Given
            Long userId = 1L;
            UserCouponCommand.UserCouponCreate command = new UserCouponCommand.UserCouponCreate(userId, new UserCouponDto.UserCouponRequest(1L));
            when(userRepository.getUser(userId)).thenReturn(null);

            // When & Then
            assertThrows(RuntimeException.class, () -> userCouponService.issueCouponToUser(command));
        }
    }
    @Nested
    @DisplayName("getUserCoupons 메서드")
    class GetUserCouponsTest {
        @Test
        @DisplayName("사용자의 쿠폰 목록 조회 성공")
        void getUserCouponsSuccess() {
            // Given
            Long userId = 1L;
            List<UserCoupon> expectedCoupons = Arrays.asList(new UserCoupon(), new UserCoupon());
            when(userCouponRepository.getAllCouponsByUserId(userId)).thenReturn(expectedCoupons);

            // When
            List<UserCoupon> result = userCouponService.getUserCoupons(userId);

            // Then
            assertNotNull(result);
            assertEquals(expectedCoupons.size(), result.size());
            verify(userCouponRepository).getAllCouponsByUserId(userId);
        }
    }
}