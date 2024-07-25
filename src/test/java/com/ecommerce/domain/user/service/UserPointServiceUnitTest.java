package com.ecommerce.domain.user.service;

import com.ecommerce.api.exception.domain.UserException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuantumLockManager quantumLockManager;

    @InjectMocks
    private UserPointService userPointService;

    private User testUser;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User(1L,"testUser", BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하는 경우")
    void getPointWhenUserExists() {
        when(userRepository.getAmountByUserId(userId)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        BigDecimal balance = userPointService.getPoint(userId);

        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getPointWhenUserNotExists() {
        when(userRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserException.ServiceException.class, () -> userPointService.getPoint(userId));
        verify(userRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 성공 케이스")
    void chargePointSuccess() throws TimeoutException {
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);

        when(quantumLockManager.executeWithLock(anyString(), any(), any())).thenReturn(BigDecimal.valueOf(2000));
        BigDecimal newBalance = userPointService.chargePoint(userId, chargeAmount);

        assertEquals(chargeAmount.add(testUser.getPoint()), newBalance);
    }

    @Test
    @DisplayName("잔액 충전 - 포인트 충전중 오류발생")
    void chargePointUserNotFound() throws TimeoutException {
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);

        when(quantumLockManager.executeWithLock(anyString(), any(Duration.class), any()))
                .thenAnswer(invocation -> ((Callable<?>) invocation.getArgument(2)).call());

        UserException.ServiceException exception = assertThrows(UserException.ServiceException.class,
                () -> userPointService.chargePoint(userId, chargeAmount));

        assertEquals("포인트 충전 중 오류 발생", exception.getMessage());
    }

    @Test
    @DisplayName("잔액 감소 - 성공 케이스")
    void deductPointSuccess() throws TimeoutException {
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);

        when(quantumLockManager.executeWithLock(anyString(), any(), any())).thenReturn(BigDecimal.valueOf(500));
        BigDecimal deductPoint = userPointService.deductPoint(userId, decreaseAmount);

        assertEquals(BigDecimal.valueOf(500), deductPoint);

    }


    @Test
    @DisplayName("잔액 감소 - 포인트 감소중 오류발생")
    void deductPointUserNotFound() throws TimeoutException {
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);

        when(quantumLockManager.executeWithLock(anyString(), any(Duration.class), any()))
                .thenAnswer(invocation -> ((Callable<?>) invocation.getArgument(2)).call());

        UserException.ServiceException exception = assertThrows(UserException.ServiceException.class,
                () -> userPointService.deductPoint(userId, decreaseAmount));

        assertEquals("포인트 감소 중 오류 발생", exception.getMessage());
    }
}