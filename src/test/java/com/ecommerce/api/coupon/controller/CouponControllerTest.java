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
        CouponCommand.CouponCreate command = new CouponCommand.CouponCreate("SUMMER2024", BigDecimal.valueOf(5000), 100, DiscountType.FIXED_AMOUNT,LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        Coupon coupon = new Coupon( "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        when(couponService.createCoupon(command)).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.discountAmount").value(5000))
                .andExpect(jsonPath("$.remainingQuantity").value(100))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testIssueCouponToUser() throws Exception {
        CouponDto.UserCouponRequest request = new CouponDto.UserCouponRequest(1L);
        User user = new User("test", BigDecimal.ZERO);
        Coupon coupon = new Coupon( "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        CouponCommand.UserCouponCreate command = new CouponCommand.UserCouponCreate(1L, request);
        UserCoupon userCoupon = new UserCoupon(user,coupon);
        when(couponService.issueCouponToUser(command)).thenReturn(userCoupon);

        mockMvc.perform(post("/api/users/1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupon.discountAmount").value(5000));
    }

    @Test
    void testGetUserCoupons() throws Exception {
        User user = new User("test", BigDecimal.ZERO);
        Coupon coupon = new Coupon( "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        Coupon coupon2 = new Coupon( "WELCOME", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        when(couponService.getUserCoupons(any(Long.class))).thenReturn(java.util.Arrays.asList(
                new UserCoupon(user,coupon),
                new UserCoupon(user,coupon2)
        ));

        mockMvc.perform(get("/api/users/1/coupons"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].coupon.code").value("SUMMER2024"))
                .andExpect(jsonPath("$[1].coupon.code").value("WELCOME"));
    }

    @Test
    void testUseCoupon() throws Exception {
        User user = new User();
        Coupon coupon = new Coupon("SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        UserCoupon userCoupon = new UserCoupon(user,coupon);
        when(couponService.useCoupon(any(Long.class), any(Long.class))).thenReturn(userCoupon);

        mockMvc.perform(post("/api/users/1/coupons/1/use"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.remainingQuantity").value(100))
                .andExpect(jsonPath("$.active").value(true));
    }
}