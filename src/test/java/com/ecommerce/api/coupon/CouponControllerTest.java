package com.ecommerce.api.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CouponController.CouponRequest sampleCouponRequest;

    @BeforeEach
    void setUp() {
        sampleCouponRequest = new CouponController.CouponRequest("SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @Test
    void testCreateCoupon() throws Exception {
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid12341234")
                        .content(objectMapper.writeValueAsString(sampleCouponRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.discountAmount").value(5000))
                .andExpect(jsonPath("$.remainingQuantity").value(100));
    }

    @Test
    void testIssueCouponToUser() throws Exception {
        mockMvc.perform(post("/api/users/1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer valid12341234")
                        .content("{\"couponId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.used").value(false));
    }

    @Test
    void testRequestCouponIssue() throws Exception {
        mockMvc.perform(post("/api/coupons/1/issue")
                        .header("Authorization", "Bearer valid12341234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("쿠폰이 성공적으로 발급되었습니다."))
                .andExpect(jsonPath("$.userCouponId").exists());
    }

    @Test
    void testGetUserCoupons() throws Exception {
        mockMvc.perform(get("/api/users/1/coupons")
                        .header("Authorization", "Bearer valid12341234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].couponId").exists())
                .andExpect(jsonPath("$[0].couponName").exists());
    }

    @Test
    void testUseCoupon() throws Exception {
        mockMvc.perform(post("/api/users/1/coupons/1/use")
                        .header("Authorization", "Bearer valid12341234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("쿠폰이 성공적으로 사용되었습니다."));
    }

    @Test
    void testGetCouponDetail() throws Exception {
        mockMvc.perform(get("/api/coupons/1")
                        .header("Authorization", "Bearer valid12341234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.discountAmount").exists());
    }
}
