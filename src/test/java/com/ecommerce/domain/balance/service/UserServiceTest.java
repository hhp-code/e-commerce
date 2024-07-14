package com.ecommerce.domain.balance.service;

import com.ecommerce.domain.user.service.UserBalanceCommand;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
import com.ecommerce.domain.user.User;
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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserBalanceRepository userBalanceRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 조회 성공 시나리오")
    void getBalanceSuccess() {
        // given
        when(userBalanceRepository.getAmountByUserId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        // when
        BigDecimal balance = userService.getBalance(1L);

        // then
        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userBalanceRepository).getAmountByUserId(1L);
    }

    @Test
    @DisplayName("잔액 조회 실패 시나리오 - 사용자 없음")
    void getBalanceUserNotFound() {
        // given
        when(userBalanceRepository.getAmountByUserId(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.getBalance(1L));
    }

    @Test
    @DisplayName("잔액 충전 성공 시나리오")
    void chargeBalanceSuccess() {
        // given
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(userId, expectedBalance)).thenReturn(Optional.of(testUser));

        UserBalanceCommand.Create request = new UserBalanceCommand.Create(userId, chargeAmount);

        // when
        BigDecimal newBalance = userService.chargeBalance(request);

        // then
        assertEquals(expectedBalance, newBalance);
        verify(userBalanceRepository).getAmountByUserId(userId);
        verify(userBalanceRepository).saveChargeAmount(userId, expectedBalance);
    }

    @Test
    @DisplayName("잔액 충전 실패 시나리오 - 사용자 없음")
    void chargeBalanceUserNotFound() {
        // given
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        UserBalanceCommand.Create request = new UserBalanceCommand.Create(userId, chargeAmount);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.chargeBalance(request));
        verify(userBalanceRepository).getAmountByUserId(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(anyLong(), any());
    }

    @Test
    @DisplayName("잔액 충전 - 여러 번 충전")
    void chargeBalanceMultipleTimes() {
        // given
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal firstCharge = BigDecimal.valueOf(1000);
        BigDecimal secondCharge = BigDecimal.valueOf(1000);

        when(userBalanceRepository.getAmountByUserId(userId))
                .thenReturn(Optional.of(initialBalance))
                .thenReturn(Optional.of(initialBalance.add(firstCharge)));

        when(userBalanceRepository.saveChargeAmount(eq(userId), any()))
                .thenReturn(Optional.of(testUser))
                .thenReturn(Optional.of(testUser));

        // when
        BigDecimal balanceAfterFirstCharge = userService.chargeBalance(new UserBalanceCommand.Create(userId, firstCharge));
        BigDecimal finalBalance = userService.chargeBalance(new UserBalanceCommand.Create(userId, secondCharge));

        // then
        assertEquals(BigDecimal.valueOf(2000), balanceAfterFirstCharge);
        assertEquals(BigDecimal.valueOf(3000), finalBalance);
        verify(userBalanceRepository, times(2)).getAmountByUserId(userId);
        verify(userBalanceRepository, times(2)).saveChargeAmount(eq(userId), any());
    }
}