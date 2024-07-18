package com.ecommerce.api.controller.domain.coupon;

import com.ecommerce.api.controller.domain.coupon.dto.CouponDto;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCouponTest() throws Exception {
        CouponDto.CouponRequest request = new CouponDto.CouponRequest(1L,
                "TEST123", BigDecimal.valueOf(1000),
                10, DiscountType.FIXED_AMOUNT,
                LocalDateTime.now(),LocalDateTime.now().plusDays(7),true );

        MvcResult result = mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST123"))
                .andExpect(jsonPath("$.discountAmount").value(1000))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        CouponDto.CouponResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CouponDto.CouponResponse.class
        );

        assertNotNull(response);
        assertEquals("TEST123", response.code());
        assertThat(response.discountAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void getCouponDetailTest() throws Exception {
        // 먼저 쿠폰을 생성
        CouponDto.CouponRequest createRequest = new CouponDto.CouponRequest(1L,
                "DETAIL123", BigDecimal.valueOf(2000),10, DiscountType.FIXED_AMOUNT, LocalDateTime.now(),LocalDateTime.now().plusDays(7),true );

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // 생성된 쿠폰의 상세 정보를 조회
        mockMvc.perform(get("/api/coupons/{couponId}",createRequest.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DETAIL123"))
                .andExpect(jsonPath("$.discountAmount").value(2000))
                .andExpect(jsonPath("$.active").exists());
    }
}