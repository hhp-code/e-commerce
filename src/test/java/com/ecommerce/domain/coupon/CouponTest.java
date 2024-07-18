package com.ecommerce.domain.coupon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private Coupon coupon;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        coupon = new Coupon("TEST10", BigDecimal.valueOf(10), DiscountType.PERCENTAGE, 5,
                now.minusDays(1), now.plusDays(6), true);
    }


    @Test
    @DisplayName("쿠폰 유효성 검사")
    void testIsValid() {
        assertTrue(coupon.isValid());

        Coupon expiredCoupon = new Coupon("EXPIRED", BigDecimal.TEN, DiscountType.FIXED_AMOUNT, 1,
                now.minusDays(2), now.minusDays(1), true);
        assertFalse(expiredCoupon.isValid());

        Coupon inactiveCoupon = new Coupon("INACTIVE", BigDecimal.TEN, DiscountType.FIXED_AMOUNT, 1,
                now.minusDays(1), now.plusDays(1), false);
        assertFalse(inactiveCoupon.isValid());

        Coupon zeroQuantityCoupon = new Coupon("ZERO", BigDecimal.TEN, DiscountType.FIXED_AMOUNT, 0,
                now.minusDays(1), now.plusDays(1), true);
        assertFalse(zeroQuantityCoupon.isValid());
    }

    @Test
    @DisplayName("수량 차감 테스트")
    void testDeductQuantity() {
        assertFalse(coupon.deductQuantity());
        assertEquals(4, coupon.getQuantity());

        for (int i = 0; i < 4; i++) {
            assertFalse(coupon.deductQuantity());
        }
        assertTrue(coupon.deductQuantity());
        assertEquals(0, coupon.getQuantity());
    }

    @Test
    @DisplayName("쿠폰 사용 테스트")
    void testUse() {
        assertTrue(coupon.getActive());
        coupon.use();
        assertFalse(coupon.getActive());
    }
}