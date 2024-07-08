package com.ecommerce.api.balance.controller;

import com.ecommerce.api.balance.service.BalanceCommand;
import com.ecommerce.api.balance.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BalanceService balanceService;

    private BalanceCommand.Create balanceRequest;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        balanceRequest = new BalanceCommand.Create(1L,BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("잔액 조회")
    void getBalance() throws Exception {
        //given
        given(balanceService.getBalance(userId)).willReturn(BigDecimal.valueOf(1000));
        //when
        mockMvc.perform(get("/api/balance/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.balance").value(1000));

        //then
        verify(balanceService).getBalance(userId);

    }

    @Test
    @DisplayName("잔액 충전")
    void chargeBalance() throws Exception {
        //given
        given(balanceService.chargeBalance(balanceRequest)).willReturn(BigDecimal.valueOf(1000));

        //when
        mockMvc.perform(post("/api/balance/{userId}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.balance").value(1000));
        //then
        verify(balanceService).chargeBalance( balanceRequest);
    }
}