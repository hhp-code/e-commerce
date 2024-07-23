package com.ecommerce.domain.user;

import com.ecommerce.api.exception.domain.UserException;
import com.ecommerce.domain.coupon.Coupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("잔액 충전 테스트")
    void testChargePoint() {
        User user = new User("testUser", BigDecimal.ZERO);
        BigDecimal chargeAmount = BigDecimal.valueOf(100);

        BigDecimal newBalance = user.chargePoint(chargeAmount);

        assertEquals(chargeAmount, newBalance);
        assertEquals(chargeAmount, user.getPoint());
    }
    @Test
    @DisplayName("잔액 감소 실패 테스트")
    void testDeductPointInsufficientFunds() {
        User user = new User("testUser", BigDecimal.valueOf(100));
        BigDecimal decreaseAmount = BigDecimal.valueOf(150);

        assertThrows(UserException.class, () -> user.deductPoint(decreaseAmount));
    }
    @Test
    @DisplayName("잔액 감소 테스트")
    void testDeductPoint() {
        User user = new User("testUser", BigDecimal.valueOf(100));
        BigDecimal decreaseAmount = BigDecimal.valueOf(50);

        BigDecimal newBalance = user.deductPoint(decreaseAmount);

        assertEquals(BigDecimal.valueOf(50), newBalance);
        assertEquals(BigDecimal.valueOf(50), user.getPoint());
    }

    @Test
    @DisplayName("쿠폰 추가 테스트")
    void testAddCoupon() {
        User user = new User("testUser", BigDecimal.ZERO);
        Coupon coupon = new Coupon();
        user.addCoupon(coupon);

        assertTrue(user.getCoupons().contains(coupon));
    }
    @Test
    @DisplayName("잔액 음수 테스트 실패")
    void testChargePointWithNegativeAmount() {
        User user = new User("testUser", BigDecimal.ZERO);
        BigDecimal negativeAmount = BigDecimal.valueOf(-50);

        assertThrows(UserException.class, () -> user.chargePoint(negativeAmount));
    }

    @Test
    @DisplayName("잔액 감소 음수 테스트 실패")
    void testDeductPointWithNegativeAmount() {
        User user = new User("testUser", BigDecimal.valueOf(100));
        BigDecimal negativeAmount = BigDecimal.valueOf(-50);

        assertThrows(UserException.class, () -> user.deductPoint(negativeAmount));
    }

}