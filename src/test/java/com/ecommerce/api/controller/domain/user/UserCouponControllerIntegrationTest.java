package com.ecommerce.api.controller.domain.user;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.UserWrite;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.infra.coupon.entity.CouponEntity;
import com.ecommerce.infra.user.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Disabled("현재는 완료되지 않았습니다.")
@SpringBootTest
@AutoConfigureMockMvc
class UserCouponControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CouponService couponService;


    private UserWrite testUser;
    private CouponWrite testCoupon;

    @BeforeEach
    @Transactional
    void setUp(){
        testCoupon = new CouponWrite( "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        couponService.saveCoupon(testCoupon);
        testUser = new UserWrite(1L,"test", BigDecimal.ZERO, List.of(testCoupon));
        userService.saveUser(testUser);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(10);
            scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler-");
            scheduler.initialize();
            return scheduler;
        }
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 성공")
    void issueCouponToUser() throws Exception {

        mockMvc.perform(post("/api/users/{userId}/coupons", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCoupon.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.coupons[0].id").value(testCoupon.getId()));
    }


}