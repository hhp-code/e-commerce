package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private QuantumLockManager quantumLockManager;


    @Mock
    private UserService userService;

    private final long userId = 1L;



    @Test
    @DisplayName("잔액 조회 - 사용자가 존재하는 경우")
    void getPointWhenUserExists() {

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






}