package com.ecommerce.domain.balance.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserBalanceCommand;
import com.ecommerce.domain.user.service.UserBalanceService;
import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBalanceServiceUnitTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @InjectMocks
    private UserBalanceService userBalanceService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하는 경우")
    void getBalanceWhenUserExists() {
        long userId = 1L;
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        BigDecimal balance = userBalanceService.getBalance(userId);

        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userBalanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getBalanceWhenUserNotExists() {
        long userId = 1L;
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userBalanceService.getBalance(userId));
        verify(userBalanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 성공 케이스")
    void chargeBalanceSuccess() {
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(eq(userId), any(BigDecimal.class))).thenReturn(Optional.of(testUser));

        BigDecimal newBalance = userBalanceService.chargeBalance(new UserBalanceCommand.Create(userId, chargeAmount));

        assertEquals(expectedBalance, newBalance);
        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository).saveChargeAmount(eq(userId), eq(expectedBalance));
    }

    @Test
    @DisplayName("잔액 충전 - 사용자가 존재하지 않는 경우")
    void chargeBalanceUserNotFound() {
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userBalanceService.chargeBalance(new UserBalanceCommand.Create(userId, chargeAmount)));

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
    }

    @Test
    @DisplayName("잔액 감소 - 성공 케이스")
    void decreaseBalanceSuccess() {
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = initialBalance.subtract(decreaseAmount);

        User user = new User(1L,"testUser", initialBalance);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(eq(userId), any(BigDecimal.class))).thenReturn(Optional.of(user));

        userBalanceService.decreaseBalance(user, decreaseAmount);

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository).saveChargeAmount(eq(userId), eq(expectedBalance));
    }

    @Test
    @DisplayName("잔액 감소 - 잔액 부족")
    void decreaseBalanceInsufficientFunds() {
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal decreaseAmount = BigDecimal.valueOf(1500);

        User user = new User(1L,"testUser", initialBalance);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));

        assertThrows(IllegalArgumentException.class, () -> userBalanceService.decreaseBalance(user, decreaseAmount));

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
    }

    @Test
    @DisplayName("잔액 감소 - 사용자가 존재하지 않는 경우")
    void decreaseBalanceUserNotFound() {
        long userId = 1L;
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);

        User user = new User(1L,"testUser", BigDecimal.ZERO);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userBalanceService.decreaseBalance(user, decreaseAmount));

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
    }
}