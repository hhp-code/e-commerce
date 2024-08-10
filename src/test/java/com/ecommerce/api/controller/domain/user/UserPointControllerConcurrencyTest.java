package com.ecommerce.api.controller.domain.user;

import com.ecommerce.application.UserFacade;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserPointControllerConcurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;


    @Autowired
    private UserFacade userPointService;

    private User testUser;

    @BeforeEach
    @Transactional
    void setUp() {
        testUser = userService.saveUser(new User("TestUser", BigDecimal.ZERO));

    }

    @Test
    @DisplayName("잔액 따닥 10번 충전")
    void chargeBalance() throws Exception {
        int taskCount = 10;

        BigDecimal amount = BigDecimal.valueOf(10);
        ExecutorService executorService = Executors.newFixedThreadPool(taskCount);
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/point/{userId}/charge", testUser.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(amount)))
                            .andExpect(request().asyncStarted())
                            .andReturn();

                    mockMvc.perform(asyncDispatch(result))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    fail("요청 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        BigDecimal expectedBalance = amount.multiply(BigDecimal.valueOf(taskCount));
        User point = userService.getPoint(testUser.getId());
        BigDecimal actualBalance = point.getPoint();

        assertThat(actualBalance).isEqualByComparingTo(expectedBalance);

        executorService.shutdown();


    }
}