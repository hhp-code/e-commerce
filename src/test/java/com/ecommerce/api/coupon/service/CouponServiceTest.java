package com.ecommerce.api.coupon.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.ecommerce.api.coupon.controller.dto.CouponDto;
import com.ecommerce.api.coupon.service.repository.CouponRepository;
import com.ecommerce.api.coupon.service.repository.UserCouponRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.DiscountType;
import com.ecommerce.api.domain.User;
import com.ecommerce.api.domain.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Nested
    @DisplayName("createCoupon 메서드")
    class CreateCouponTest {

        @Test
        @DisplayName("쿠폰 생성 성공")
        void createCouponSuccess() {
            // Given
            CouponCommand.CouponCreate command = new CouponCommand.CouponCreate(
                    "CODE123", BigDecimal.TEN, 100, DiscountType.FIXED_AMOUNT,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(30), true
            );
            Coupon expectedCoupon = new Coupon(command.code(), command.discountAmount(),
                    command.type(), command.remainingQuantity(), command.validFrom(),
                    command.validTo(), command.active());

            when(couponRepository.save(any(Coupon.class))).thenReturn(Optional.of(expectedCoupon));

            // When
            Coupon result = couponService.createCoupon(command);

            // Then
            assertNotNull(result);
            assertEquals(command.code(), result.getCode());
            verify(couponRepository).save(any(Coupon.class));
        }

        @Test
        @DisplayName("쿠폰 생성 실패")
        void createCouponFailure() {
            // Given
            CouponCommand.CouponCreate command = new CouponCommand.CouponCreate(
                    "CODE123", BigDecimal.TEN, 100, DiscountType.FIXED_AMOUNT,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(30), true
            );

            when(couponRepository.save(any(Coupon.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> couponService.createCoupon(command));
        }
    }

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
            CouponDto.UserCouponRequest couponRequest = new CouponDto.UserCouponRequest(couponId);
            CouponCommand.UserCouponCreate command = new CouponCommand.UserCouponCreate(userId, couponRequest);

            when(userRepository.getById(userId)).thenReturn(Optional.of(user));
            when(couponRepository.getById(couponId)).thenReturn(Optional.of(coupon));
            when(userCouponRepository.getCouponByUser(user, coupon)).thenReturn(Optional.of(userCoupon));

            // When
            UserCoupon result = couponService.issueCouponToUser(command);

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
            CouponDto.UserCouponRequest couponRequest = new CouponDto.UserCouponRequest(1L);

            when(userRepository.getById(userId)).thenReturn(Optional.empty());
            CouponCommand.UserCouponCreate command = new CouponCommand.UserCouponCreate(userId, couponRequest);

            when(userRepository.getById(userId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> couponService.issueCouponToUser(command));
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
            List<UserCoupon> result = couponService.getUserCoupons(userId);

            // Then
            assertNotNull(result);
            assertEquals(expectedCoupons.size(), result.size());
            verify(userCouponRepository).getAllCouponsByUserId(userId);
        }
    }

    @Nested
    @DisplayName("useCoupon 메서드")
    class UseCouponTest {

        @Test
        @DisplayName("쿠폰 사용 성공")
        void useCouponSuccess() {
            // Given
            Long userId = 1L;
            Long couponId = 1L;
            User user = new User("test", BigDecimal.ZERO);
            Coupon coupon = new Coupon("CODE123", BigDecimal.TEN,  DiscountType.FIXED_AMOUNT,100,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
            UserCoupon userCoupon = new UserCoupon(user, coupon);

            when(userRepository.getById(userId)).thenReturn(Optional.of(user));
            when(couponRepository.getById(couponId)).thenReturn(Optional.of(coupon));
            when(userCouponRepository.getCouponByUser(user, coupon)).thenReturn(Optional.of(userCoupon));
            when(userCouponRepository.save(userCoupon)).thenReturn(Optional.of(userCoupon));

            // When
            UserCoupon result = couponService.useCoupon(userId, couponId);

            // Then
            assertNotNull(result);
            assertTrue(result.isUsed());
            verify(userCouponRepository).save(userCoupon);
        }

        @Test
        @DisplayName("이미 사용된 쿠폰 사용 시도")
        void useCouponAlreadyUsed() {
            // Given
            Long userId = 1L;
            Long couponId = 1L;
            User user = new User();
            Coupon coupon = new Coupon();
            UserCoupon userCoupon = new UserCoupon(user, coupon);
            userCoupon.use(); // 쿠폰을 이미 사용 상태로 설정

            when(userRepository.getById(userId)).thenReturn(Optional.of(user));
            when(couponRepository.getById(couponId)).thenReturn(Optional.of(coupon));
            when(userCouponRepository.getCouponByUser(user, coupon)).thenReturn(Optional.of(userCoupon));

            // When & Then
            assertThrows(RuntimeException.class, () -> couponService.useCoupon(userId, couponId));
        }
    }
}