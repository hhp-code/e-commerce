package com.ecommerce.api.balance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
public class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetBalance() throws Exception {
        mockMvc.perform(get("/api/balance/1")
                .header("Authorization", "Bearer valid12341234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("잔액 조회 성공"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.balance").value(5000));
    }

    @Test
    public void testChargeBalance() throws Exception {
        Map<String, BigDecimal> requestBody = new HashMap<>();
        requestBody.put("amount", new BigDecimal("5000"));

        mockMvc.perform(post("/api/users/1/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid12341234")
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(5000))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    public void testChargeBalanceInvalidAmount() throws Exception {
        Map<String, BigDecimal> requestBody = new HashMap<>();
        requestBody.put("amount", new BigDecimal("500"));

        mockMvc.perform(post("/api/users/1/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid12341234")
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("충전 금액은 1,000원 이상 1,000,000원 이하여야 합니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CHARGE_AMOUNT"));
    }
}