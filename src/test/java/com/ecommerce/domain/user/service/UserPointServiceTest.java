package com.ecommerce.domain.user.service;

import com.ecommerce.api.exception.domain.UserException;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserPointServiceTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserService userService;

    private long userId;

    @BeforeEach
    void setup(){
        User testUser = new User("testUser", BigDecimal.valueOf(1000));
        User savedUser = userService.saveUser(testUser);
        userId = savedUser.getId();
    }


    @Test
    @DisplayName("잔액 조회 성공 시나리오")
    void getPointSuccess() {
        // given &when
        BigDecimal balance = userPointService.getPoint(userId);

        // then
        assertThat(balance).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("잔액 조회 실패 시나리오 - 사용자 없음")
    void getPointUserNotFound() {
        // given
        long userId = 999L;
        // when & then
        assertThrows(UserException.ServiceException.class, () -> userPointService.getPoint(userId));
    }

    @Test
    @DisplayName("잔액 충전 성공 시나리오")
    void chargePointSuccess() {
        // given
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        // when
        BigDecimal newBalance = userPointService.chargePoint(userId,chargeAmount);

        // then
        assertThat(newBalance).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("잔액 충전 실패 시나리오 - 사용자 없음")
    void chargePointUserNotFound() {
        // given
        long userId = 999L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);


        // when & then
        assertThrows(UserException.ServiceException.class,
                () -> userPointService.chargePoint(userId, chargeAmount));
    }

    @Test
    @DisplayName("잔액 충전 - 여러 번 충전")
    void chargePointMultipleTimes() {
        // given
        BigDecimal firstCharge = BigDecimal.valueOf(1000);
        BigDecimal secondCharge = BigDecimal.valueOf(1000);

        // when
        BigDecimal balanceAfterFirstCharge = userPointService.chargePoint(userId, firstCharge);
        BigDecimal finalBalance = userPointService.chargePoint(userId, secondCharge);

        // then
        assertThat(balanceAfterFirstCharge).isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(finalBalance).isEqualByComparingTo(BigDecimal.valueOf(3000));
    }

}