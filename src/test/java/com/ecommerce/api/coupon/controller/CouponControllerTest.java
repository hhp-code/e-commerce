package com.ecommerce.api.coupon.controller;

import com.ecommerce.api.coupon.controller.dto.CouponDto;
import com.ecommerce.api.coupon.service.CouponCommand;
import com.ecommerce.api.coupon.service.CouponService;
import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.DiscountType;
import com.ecommerce.api.domain.User;
import com.ecommerce.api.domain.UserCoupon;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;

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
    void testCreateCoupon() throws Exception {
        CouponDto.CouponRequest request = new CouponDto.CouponRequest("SUMMER2024", BigDecimal.valueOf(5000), 100, DiscountType.FIXED_AMOUNT, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        Coupon coupon = new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);

        when(couponService.createCoupon(any(CouponCommand.CouponCreate.class))).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.discountAmount").value(5000))
                .andExpect(jsonPath("$.remainingQuantity").value(100))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testIssueCouponToUser() throws Exception {
        Long userId = 1L;
        Long couponId = 1L;
        CouponDto.UserCouponRequest request = new CouponDto.UserCouponRequest(couponId);
        UserCoupon userCoupon = new UserCoupon(new User(userId, "test", BigDecimal.ZERO),
                new Coupon(couponId, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true));

        when(couponService.issueCouponToUser(any(CouponCommand.UserCouponCreate.class))).thenReturn(userCoupon);

        mockMvc.perform(post("/api/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.coupon.id").value(couponId))
                .andExpect(jsonPath("$.coupon.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupon.discountAmount").value(5000));
    }

    @Test
    void testGetUserCoupons() throws Exception {
        long userId = 1L;
        List<UserCoupon> userCoupons = Arrays.asList(
                new UserCoupon(new User(userId, "test", BigDecimal.ZERO),
                        new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true)),
                new UserCoupon(new User(userId, "test", BigDecimal.ZERO),
                        new Coupon(2L, "WELCOME", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true))
        );

        when(couponService.getUserCoupons(userId)).thenReturn(userCoupons);

        mockMvc.perform(get("/api/users/{userId}/coupons", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("SUMMER2024"))
                .andExpect(jsonPath("$[1].code").value("WELCOME"));
    }

    @Test
    void testUseCoupon() throws Exception {
        Long userId = 1L;
        Long userCouponId = 1L;
        UserCoupon usedCoupon = new UserCoupon(
                new User(userId, "test", BigDecimal.ZERO),
                new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true)
        );

        when(couponService.useCoupon(userId, userCouponId)).thenReturn(usedCoupon);

        mockMvc.perform(post("/api/users/{userId}/coupons/{userCouponId}/use", userId, userCouponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.remainingQuantity").value(100))
                .andExpect(jsonPath("$.active").value(true));
    }

}