package com.ecommerce.api.balance.controller;

import com.ecommerce.api.balance.service.BalanceCommand;
import com.ecommerce.api.balance.service.BalanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    private static final String API_BALANCE = "/api/balance";
    private static final String API_BALANCE_CHARGE = "/api/balance/{userId}/charge";
    private static final Long USER_ID = 1L;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BalanceService balanceService;

    private BalanceCommand.Create balanceRequest;

    @BeforeEach
    void setUp() {
        balanceRequest = new BalanceCommand.Create(USER_ID, INITIAL_BALANCE);
    }

    @Test
    @DisplayName("잔액 조회 성공")
    void getBalance_ShouldReturnBalance_WhenUserExists() throws Exception {
        given(balanceService.getBalance(USER_ID)).willReturn(INITIAL_BALANCE);

        mockMvc.perform(get(API_BALANCE + "/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.balance").value(INITIAL_BALANCE.intValue()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(balanceService).getBalance(USER_ID);
    }

    @Test
    @DisplayName("잔액 조회 실패 - 사용자 없음")
    void getBalance_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        given(balanceService.getBalance(USER_ID)).willThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get(API_BALANCE + "/{userId}", USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(balanceService).getBalance(USER_ID);
    }

    @Test
    @DisplayName("잔액 충전 성공")
    void chargeBalance_ShouldReturnUpdatedBalance_WhenRequestIsValid() throws Exception {
        given(balanceService.chargeBalance(any(BalanceCommand.Create.class))).willReturn(INITIAL_BALANCE);

        mockMvc.perform(post(API_BALANCE_CHARGE, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.balance").value(INITIAL_BALANCE.intValue()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(balanceService).chargeBalance(any(BalanceCommand.Create.class));
    }

    @Test
    @DisplayName("잔액 충전 실패 - 잘못된 요청")
    void chargeBalance_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        BalanceCommand.Create invalidRequest = new BalanceCommand.Create(USER_ID, BigDecimal.valueOf(-1000));

        mockMvc.perform(post(API_BALANCE_CHARGE, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }
}