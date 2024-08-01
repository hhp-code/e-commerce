package com.ecommerce.api.controller.domain.user;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserPointController.class)
class UserPointControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserPointService userPointService;

    @BeforeEach
    void setUp() {
        when(userPointService.chargePoint(any(), any())).thenReturn(BigDecimal.valueOf(1000));
        when(userPointService.getPoint(any())).thenReturn(BigDecimal.ZERO);

    }

    @Test
    @DisplayName("잔액 조회")
    void getBalance() throws Exception {
        mockMvc.perform(get("/api/point/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(0));
    }

    @Test
    @DisplayName("잔액 충전")
    void chargeBalance() throws Exception {
        long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1000);
        mockMvc.perform(post("/api/point/{userId}/charge", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(1000));
    }
}