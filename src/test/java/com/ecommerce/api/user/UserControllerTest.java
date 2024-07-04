package com.ecommerce.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetUserInfo() throws Exception {
        mockMvc.perform(get("/users/1")
                        .header("Authorization", "Bearer valid12341234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.version").isNumber())
                .andExpect(jsonPath("$.isDeleted").isBoolean())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    public void testChargeBalance() throws Exception {
        UserController.BalanceRequest request = new UserController.BalanceRequest(BigDecimal.valueOf(5000));

        mockMvc.perform(post("/users/1/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid12341234")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.version").isNumber());
    }

    @Test
    public void testGetBalance() throws Exception {
        mockMvc.perform(get("/users/balance/1")
                        .header("Authorization","Bearer valid12341234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.balance").isNumber());
    }
}