package com.ecommerce.api.controller.domain.coupon;

import com.ecommerce.api.controller.domain.coupon.dto.CouponDto;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;



    @Test
    @DisplayName("쿠폰 생성 - 성공")
    void testCreateCoupon() throws Exception {
        CouponDto.CouponRequest request = new CouponDto.CouponRequest(1L,"SUMMER2024", BigDecimal.valueOf(5000), 100, DiscountType.FIXED_AMOUNT, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        Coupon coupon = new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);

        when(couponService.createCoupon(any(CouponCommand.Create.class))).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.discountAmount").value(5000))
                .andExpect(jsonPath("$.active").value(true));
    }
    @Test
    @DisplayName("쿠폰 조회 - 성공")
    void testGetCouponDetail() throws Exception {
        Coupon coupon = new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);

        when(couponService.getCoupon(1L)).thenReturn(coupon);

        mockMvc.perform(get("/api/coupons/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.discountAmount").value(5000))
                .andExpect(jsonPath("$.active").value(true));
    }





}