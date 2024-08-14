package com.ecommerce.domain.user;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.interfaces.exception.domain.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("잔액 충전 테스트")
    void testChargePoint() {
        UserWrite user = new UserWrite("testUser", BigDecimal.ZERO);
        BigDecimal chargeAmount = BigDecimal.valueOf(100);

        UserWrite user1 = user.chargePoint(chargeAmount);
        BigDecimal newBalance = user1.getPoint();

        assertEquals(chargeAmount, newBalance);
        assertEquals(chargeAmount, user.getPoint());
    }
    @Test
    @DisplayName("잔액 감소 실패 테스트")
    void testDeductPointInsufficientFunds() {
        UserWrite user = new UserWrite("testUser", BigDecimal.valueOf(100));
        BigDecimal decreaseAmount = BigDecimal.valueOf(150);

        assertThrows(UserException.class, () -> user.deductPoint(decreaseAmount));
    }
    @Test
    @DisplayName("잔액 감소 테스트")
    void testDeductPoint() {
        UserWrite user = new UserWrite("testUser", BigDecimal.valueOf(100));
        BigDecimal decreaseAmount = BigDecimal.valueOf(50);

        UserWrite user1 = user.deductPoint(decreaseAmount);
        BigDecimal newBalance = user1.getPoint();

        assertEquals(BigDecimal.valueOf(50), newBalance);
        assertEquals(BigDecimal.valueOf(50), user.getPoint());
    }

    @Test
    @DisplayName("쿠폰 추가 테스트")
    void testAddCoupon() {
        UserWrite user = new UserWrite("testUser", BigDecimal.ZERO);
        CouponWrite coupon = new CouponWrite();
        user.addCoupon(coupon);

        assertTrue(user.getCoupons().contains(coupon));
    }
    @Test
    @DisplayName("잔액 음수 테스트 실패")
    void testChargePointWithNegativeAmount() {
        UserWrite user = new UserWrite("testUser", BigDecimal.ZERO);
        BigDecimal negativeAmount = BigDecimal.valueOf(-50);

        assertThrows(UserException.class, () -> user.chargePoint(negativeAmount));
    }

    @Test
    @DisplayName("잔액 감소 음수 테스트 실패")
    void testDeductPointWithNegativeAmount() {
        UserWrite user = new UserWrite("testUser", BigDecimal.valueOf(100));
        BigDecimal negativeAmount = BigDecimal.valueOf(-50);

        assertThrows(UserException.class, () -> user.deductPoint(negativeAmount));
    }

}