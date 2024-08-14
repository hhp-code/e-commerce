package com.ecommerce.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.repository.CouponRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceUnitTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 생성 성공")
    void createCouponSuccess() {
        // Given
        CouponCommand.Create command = new CouponCommand.Create(
                "CODE123", BigDecimal.TEN, 100, DiscountType.FIXED_AMOUNT,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true
        );
        CouponWrite expectedCoupon = new CouponWrite(command.code(), command.discountAmount(),
                command.type(), command.quantity(), command.validFrom(),
                command.validTo(), command.active());


        // When
        CouponWrite result = couponService.createCoupon(command);

        // Then
        assertNotNull(result);
        assertEquals(command.code(), result.getCode());
    }

    @Test
    @DisplayName("쿠폰 생성 실패")
    void createCouponFailure() {
        // Given
        CouponCommand.Create command = new CouponCommand.Create(
                "CODE123", BigDecimal.TEN, 100, DiscountType.FIXED_AMOUNT,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true
        );

        // When & Then
        assertThrows(RuntimeException.class, () -> couponService.createCoupon(command));
    }


    @Test
    @DisplayName("특정 쿠폰 조회")
    void getCouponSuccess() {
        // Given
        Long couponId = 1L;

        // When
        CouponWrite result = couponService.getCoupon(couponId);

        // Then
        assertNotNull(result);
    }
}