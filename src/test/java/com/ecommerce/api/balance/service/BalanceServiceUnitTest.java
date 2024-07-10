package com.ecommerce.api.balance.service;

import com.ecommerce.api.balance.service.repository.BalanceRepository;
import com.ecommerce.api.domain.User;
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
class BalanceServiceUnitTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

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
        when(balanceRepository.getAmountByUserId(userId)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        // when
        BigDecimal balance = balanceService.getBalance(userId);

        // then
        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(balanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getBalanceWhenUserNotExists() {
        // given
        long userId = 1L;
        when(balanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> balanceService.getBalance(userId));
        verify(balanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 성공 케이스")
    void chargeBalanceSuccess() {
        // given
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);
        when(balanceRepository.getUserByRequest(userId)).thenReturn(Optional.of(testUser));

        // when
        BigDecimal newBalance = balanceService.chargeBalance(new BalanceCommand.Create(userId, chargeAmount));

        // then
        assertEquals(chargeAmount, newBalance);
        verify(balanceRepository).getUserByRequest(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 사용자가 존재하지 않는 경우")
    void chargeBalanceUserNotFound() {
        // given
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);
        when(balanceRepository.getUserByRequest(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> balanceService.chargeBalance(new BalanceCommand.Create(userId, chargeAmount)));
        verify(balanceRepository).getUserByRequest(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 여러 번 충전")
    void chargeBalanceMultipleTimes() {
        // given
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        testUser.setBalance(initialBalance);
        when(balanceRepository.getUserByRequest(userId)).thenReturn(Optional.of(testUser));

        // when
        balanceService.chargeBalance(new BalanceCommand.Create(userId, BigDecimal.valueOf(1000)));
        BigDecimal finalBalance = balanceService.chargeBalance(new BalanceCommand.Create(userId, BigDecimal.valueOf(1000)));

        // then
        assertEquals(BigDecimal.valueOf(3000), finalBalance);
        verify(balanceRepository, times(2)).getUserByRequest(userId);
    }
}