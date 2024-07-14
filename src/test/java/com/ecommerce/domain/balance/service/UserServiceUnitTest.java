package com.ecommerce.domain.balance.service;

import com.ecommerce.domain.user.service.UserBalanceCommand;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하는 경우")
    void getBalanceWhenUserExists() {
        // given
        long userId = 1L;
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        // when
        BigDecimal balance = userService.getBalance(userId);

        // then
        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userBalanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getBalanceWhenUserNotExists() {
        // given
        long userId = 1L;
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.getBalance(userId));
        verify(userBalanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 성공 케이스")
    void chargeBalanceSuccess() {
        // given
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(userId, expectedBalance)).thenReturn(Optional.of(testUser));

        // when
        BigDecimal newBalance = userService.chargeBalance(new UserBalanceCommand.Create(userId, chargeAmount));

        // then
        assertEquals(expectedBalance, newBalance);
        verify(userBalanceRepository).getAmountByUserId(userId);
        verify(userBalanceRepository).saveChargeAmount(userId, expectedBalance);
    }

    @Test
    @DisplayName("잔액 충전 - 사용자가 존재하지 않는 경우")
    void chargeBalanceUserNotFound() {
        // given
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> userService.chargeBalance(new UserBalanceCommand.Create(userId, chargeAmount)));
        verify(userBalanceRepository).getAmountByUserId(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
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