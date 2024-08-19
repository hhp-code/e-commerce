package com.ecommerce.domain.user;

import com.ecommerce.interfaces.exception.domain.UserException;
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

        User user1 = user.chargePoint(chargeAmount);
        BigDecimal newBalance = user1.getPoint();

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

        User user1 = user.deductPoint(decreaseAmount);
        BigDecimal newBalance = user1.getPoint();

        assertEquals(BigDecimal.valueOf(50), newBalance);
        assertEquals(BigDecimal.valueOf(50), user.getPoint());
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