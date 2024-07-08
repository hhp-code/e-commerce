package com.ecommerce.api.balance.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;

class BalanceServiceUnitTest {


    @InjectMocks
    private BalanceService balanceService;

    @Test
    @DisplayName("잔액 조회 결과값이 없는 경우")
    void getBalance() {
        //given
        long userId = 1L;
        //when
        BigDecimal balance = balanceService.getBalance(userId);

        //then
        balance.equals(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액 충전")
    void chargeBalance() {
        //given
        long userId = 1L;
        //when
        balanceService.chargeBalance(new BalanceCommand.Create(userId, BigDecimal.valueOf(1000)));
        balanceService.chargeBalance(new BalanceCommand.Create(userId, BigDecimal.valueOf(1000)));
        //then
        balanceService.getBalance(userId).equals(BigDecimal.valueOf(2000));
    }




}