package com.ecommerce.domain.user;

import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserPointServiceTest {

    @Autowired
    private UserPointService userPointService;

    @MockBean
    private UserRepository userBalanceRepository;

    private User testUser;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        testUser = new User(1L,"testUser", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 조회 성공 시나리오")
    void getPointSuccess() {
        // given
        when(userBalanceRepository.getAmountByUserId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        // when
        BigDecimal balance = userPointService.getPoint(1L);

        // then
        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userBalanceRepository).getAmountByUserId(1L);
    }

    @Test
    @DisplayName("잔액 조회 실패 시나리오 - 사용자 없음")
    void getPointUserNotFound() {
        // given
        when(userBalanceRepository.getAmountByUserId(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userPointService.getPoint(1L));
    }

    @Test
    @DisplayName("잔액 충전 성공 시나리오")
    void chargePointSuccess() {
        // given
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(userId, expectedBalance)).thenReturn(Optional.of(testUser));


        // when
        BigDecimal newBalance = userPointService.chargePoint(userId,chargeAmount);

        // then
        assertEquals(expectedBalance, newBalance);
        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository).saveChargeAmount(userId, expectedBalance);
    }

    @Test
    @DisplayName("잔액 충전 실패 시나리오 - 사용자 없음")
    void chargePointUserNotFound() {
        // given
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());


        // when & then
        assertThrows(IllegalArgumentException.class, () -> userPointService.chargePoint(userId,chargeAmount));
        verify(userBalanceRepository, never()).saveChargeAmount(anyLong(), any());
    }

    @Test
    @DisplayName("잔액 충전 - 여러 번 충전")
    void chargePointMultipleTimes() {
        // given
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal firstCharge = BigDecimal.valueOf(1000);
        BigDecimal secondCharge = BigDecimal.valueOf(1000);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId))
                .thenReturn(Optional.of(initialBalance))
                .thenReturn(Optional.of(initialBalance.add(firstCharge)));

        when(userBalanceRepository.saveChargeAmount(eq(userId), any()))
                .thenReturn(Optional.of(testUser))
                .thenReturn(Optional.of(testUser));

        // when
        BigDecimal balanceAfterFirstCharge = userPointService.chargePoint(userId, firstCharge);
        BigDecimal finalBalance = userPointService.chargePoint(userId, secondCharge);

        // then
        assertEquals(BigDecimal.valueOf(2000), balanceAfterFirstCharge);
        assertEquals(BigDecimal.valueOf(3000), finalBalance);
        verify(userBalanceRepository, times(2)).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, times(2)).saveChargeAmount(eq(userId), any());
    }
}