package com.ecommerce.domain.user.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.UserFacade;
import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class UserPointServiceTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private UserFacade userFacade;

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
        User point = userService.getPoint(userId);
        BigDecimal balance = point.getPoint();

        // then
        assertThat(balance).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("잔액 조회 실패 시나리오 - 사용자 없음")
    void getPointUserNotFound() {
        // given
        long userId = 999L;
        // when & then
        assertThrows(UserException.ServiceException.class, () -> userService.getPoint(userId));
    }

    @Test
    @DisplayName("잔액 충전 성공 시나리오")
    void chargePointSuccess() {
        // given
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        // when
        User user = userFacade.chargePoint(userId, chargeAmount);
        BigDecimal newBalance =  user.getPoint();

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
                () -> userFacade.chargePoint(userId, chargeAmount));
    }

    @Test
    @DisplayName("잔액 충전 - 여러 번 충전")
    void chargePointMultipleTimes() {
        // given
        BigDecimal firstCharge = BigDecimal.valueOf(1000);
        BigDecimal secondCharge = BigDecimal.valueOf(1000);

        // when
        User user = userFacade.chargePoint(userId, firstCharge);
        BigDecimal balanceAfterFirstCharge = user.getPoint();
        User user1 = userFacade.chargePoint(userId, secondCharge);
        BigDecimal finalBalance = user1.getPoint();

        // then
        assertThat(balanceAfterFirstCharge).isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(finalBalance).isEqualByComparingTo(BigDecimal.valueOf(3000));
    }

}