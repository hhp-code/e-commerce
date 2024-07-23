package com.ecommerce.domain.user.service;

import com.ecommerce.api.exception.domain.UserException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceUnitTest {

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.getAmountByUserId(userId)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        BigDecimal balance = userPointService.getPoint(userId);

        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getPointWhenUserNotExists() {
        long userId = 1L;
        when(userRepository.getAmountByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserException.ServiceException.class, () -> userPointService.getPoint(userId));
        verify(userRepository).getAmountByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 성공 케이스")
    void chargePointSuccess() {
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);

        when(userRepository.getByIdWithLock(userId)).thenReturn(Optional.ofNullable(testUser));


        BigDecimal newBalance = userPointService.chargePoint(userId, chargeAmount);

        assertEquals(chargeAmount, newBalance);
    }

    @Test
    @DisplayName("잔액 충전 - 사용자가 존재하지 않는 경우")
    void chargePointUserNotFound() {
        long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(1000);
        when(userRepository.getByIdWithLock(userId)).thenReturn(Optional.empty());

        assertThrows(UserException.ServiceException.class,
                () -> userPointService.chargePoint(userId, chargeAmount));

    }

    @Test
    @DisplayName("잔액 감소 - 성공 케이스")
    void deductPointSuccess() {
        long userId = 1L;
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);


        when(userRepository.getByIdWithLock(userId)).thenReturn(
                Optional.of(new User("testUser", BigDecimal.valueOf(1000)))
        );

        userPointService.deductPoint(userId, decreaseAmount);

    }


    @Test
    @DisplayName("잔액 감소 - 사용자가 존재하지 않는 경우")
    void deductPointUserNotFound() {
        long userId = 1L;
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);


        when(userRepository.getByIdWithLock(userId)).thenReturn(Optional.empty());

        assertThrows(UserException.ServiceException.class, () -> userPointService.deductPoint(userId,decreaseAmount));

    }
}