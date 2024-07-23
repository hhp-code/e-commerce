package com.ecommerce.domain.user;

import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.repository.UserRepository;
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
class UserPointServiceUnitTest {

    @Mock
    private UserRepository userBalanceRepository;

    @InjectMocks
    private UserPointService userPointService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하는 경우")
    void getPointWhenUserExists() {
        long userId = 1L;
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        BigDecimal balance = userPointService.getPoint(userId);

        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userBalanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getPointWhenUserNotExists() {
        long userId = 1L;
        when(userBalanceRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userPointService.getPoint(userId));
        verify(userBalanceRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 성공 케이스")
    void chargePointSuccess() {
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal chargeAmount = BigDecimal.valueOf(2000);
        BigDecimal expectedBalance = initialBalance.add(chargeAmount);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(eq(userId), any(BigDecimal.class))).thenReturn(Optional.of(testUser));

        BigDecimal newBalance = userPointService.chargePoint(userId, chargeAmount);

        assertEquals(expectedBalance, newBalance);
        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository).saveChargeAmount(eq(userId), eq(expectedBalance));
    }

    @Test
    @DisplayName("잔액 충전 - 사용자가 존재하지 않는 경우")
    void chargePointUserNotFound() {
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userPointService.chargePoint(userId, chargeAmount));

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
    }

    @Test
    @DisplayName("잔액 감소 - 성공 케이스")
    void deductPointSuccess() {
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = initialBalance.subtract(decreaseAmount);

        User user = new User(1L,"testUser", initialBalance);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));
        when(userBalanceRepository.saveChargeAmount(eq(userId), any(BigDecimal.class))).thenReturn(Optional.of(user));

        userPointService.deductPoint(1L, decreaseAmount);

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository).saveChargeAmount(eq(userId), eq(expectedBalance));
    }

    @Test
    @DisplayName("잔액 감소 - 잔액 부족")
    void deductPointInsufficientFunds() {
        long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal decreaseAmount = BigDecimal.valueOf(1500);

        User user = new User(1L,"testUser", initialBalance);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.of(initialBalance));

        assertThrows(IllegalArgumentException.class, () -> userPointService.deductPoint(1L, decreaseAmount));

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
    }

    @Test
    @DisplayName("잔액 감소 - 사용자가 존재하지 않는 경우")
    void deductPointUserNotFound() {
        long userId = 1L;
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);

        User user = new User(1L,"testUser", BigDecimal.ZERO);

        when(userBalanceRepository.getAmountByUserIdWithLock(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userPointService.deductPoint(1L, decreaseAmount));

        verify(userBalanceRepository).getAmountByUserIdWithLock(userId);
        verify(userBalanceRepository, never()).saveChargeAmount(any(), any());
    }
}