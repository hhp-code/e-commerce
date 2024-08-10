package com.ecommerce.domain.user.service;

import com.ecommerce.application.UserFacade;
import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuantumLockManager quantumLockManager;

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;

    private final long userId = 1L;



    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하는 경우")
    void getPointWhenUserExists() {
        when(userRepository.getUser(userId)).thenReturn(Optional.of(new User("testUser", BigDecimal.valueOf(1000))));

        User point = userService.getPoint(userId);
        BigDecimal balance = point.getPoint();

        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(userRepository).getUser(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하지 않는 경우")
    void getPointWhenUserNotExists() {
        when(userRepository.getUser(userId)).thenReturn(Optional.empty());

        assertThrows(UserException.ServiceException.class, () -> userService.getPoint(userId));
        verify(userRepository).getUser(userId);
    }



    @Test
    @DisplayName("잔액 감소 - 성공 케이스")
    void deductPointSuccess() throws TimeoutException {
        BigDecimal decreaseAmount = BigDecimal.valueOf(500);

        when(quantumLockManager.executeWithLock(anyString(), any(), any())).thenReturn(new User(1L,"testUser", BigDecimal.valueOf(500)));
        User user = userFacade.deductPoint(userId, decreaseAmount);
        BigDecimal deductPoint = user.getPoint();

        assertEquals(BigDecimal.valueOf(500), deductPoint);

    }


}