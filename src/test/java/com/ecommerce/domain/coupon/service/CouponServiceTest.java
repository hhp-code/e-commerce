package com.ecommerce.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.repository.CouponRepository;

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

    @InjectMocks
    private CouponService couponService;

    @Nested
    @DisplayName("createCoupon 메서드")
    class CreateCouponTest {
        @Test
        @DisplayName("쿠폰 생성 성공")
        void createCouponSuccess() {
            // Given
            CouponCommand.Create command = new CouponCommand.Create(
                    "CODE123", BigDecimal.TEN,  100,DiscountType.FIXED_AMOUNT,
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
            CouponCommand.Create command = new CouponCommand.Create(
                    "CODE123", BigDecimal.TEN, 100, DiscountType.FIXED_AMOUNT,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(30), true
            );
            when(couponRepository.save(any(Coupon.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> couponService.createCoupon(command));
        }
    }


    @Test
    @DisplayName("getCoupon 메서드")
    void getCouponSuccess() {
        // Given
        Long couponId = 1L;
        Coupon expectedCoupon = new Coupon();
        when(couponRepository.getById(couponId)).thenReturn(Optional.of(expectedCoupon));

        // When
        Coupon result = couponService.getCoupon(couponId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCoupon, result);
    }
}