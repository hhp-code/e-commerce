package com.ecommerce.api.controller.domain.user;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    @Transactional
    void setUp(){
        couponService.deleteAll();
        userService.deleteAll();
        Coupon testCoupon = new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        couponService.save(testCoupon);
        Coupon coupon = couponService.getCoupon(1L);
        System.out.println(coupon.getId()+"couponId");
        User user = new User(1L,"test", BigDecimal.ZERO, List.of(testCoupon));
        User savedUser = userService.saveUser(user);
        Long id = user.getId();
        System.out.println(id+"userId");
        System.out.println(savedUser.getId()+"savedUserId");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TaskScheduler taskScheduler() {
            return new ConcurrentTaskScheduler();
        }
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 성공")
    void issueCouponToUser() throws Exception {
        Long userId = 1L;
        Long couponId = 1L;

        mockMvc.perform(post("/api/users/{userId}/coupons", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.couponId").value(couponId));
    }

}