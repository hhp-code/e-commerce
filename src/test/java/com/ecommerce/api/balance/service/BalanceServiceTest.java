package com.ecommerce.api.balance.service;

import com.ecommerce.api.balance.service.repository.BalanceRepository;
import com.ecommerce.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BalanceServiceTest {

    @Autowired
    private BalanceService balanceService;

    @MockBean
    private BalanceRepository balanceRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setBalance(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 조회 성공 시나리오")
    void getBalanceSuccess() {
        // given
        when(balanceRepository.getAmountByUserId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(1000)));

        // when
        BigDecimal balance = balanceService.getBalance(1L);

        // then
        assertEquals(BigDecimal.valueOf(1000), balance);
        verify(balanceRepository).getAmountByUserId(1L);
    }

    @Test
    @DisplayName("잔액 조회 실패 시나리오 - 사용자 없음")
    void getBalanceUserNotFound() {
        // given
        when(balanceRepository.getAmountByUserId(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> balanceService.getBalance(1L));
    }

    @Test
    @DisplayName("잔액 충전 성공 시나리오")
    void chargeBalanceSuccess() {
        // given
        when(balanceRepository.getUserByRequest(1L)).thenReturn(Optional.of(testUser));
        BalanceCommand.Create request = new BalanceCommand.Create(1L, BigDecimal.valueOf(1000));

        // when
        BigDecimal newBalance = balanceService.chargeBalance(request);

        // then
        assertEquals(BigDecimal.valueOf(1000), newBalance);
        verify(balanceRepository).getUserByRequest(1L);
    }

    @Test
    @DisplayName("잔액 충전 실패 시나리오 - 사용자 없음")
    void chargeBalanceUserNotFound() {
        // given
        when(balanceRepository.getUserByRequest(anyLong())).thenReturn(Optional.empty());
        BalanceCommand.Create request = new BalanceCommand.Create(1L, BigDecimal.valueOf(1000));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> balanceService.chargeBalance(request));
    }
}