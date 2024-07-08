package com.ecommerce.api.coupon.controller;

import com.ecommerce.api.coupon.controller.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Tag(name = "coupon", description = "쿠폰 관련 API")
@RestController
@RequestMapping("/api")
public class CouponController {

    @PostMapping("/coupons")
    public CouponResponse createCoupon(@RequestBody CouponRequest request) {
        return new CouponResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @PostMapping("/users/{userId}/coupons")
    public UserCouponResponse issueCouponToUser(@PathVariable Long userId, @RequestBody UserCouponRequest request) {
        return new UserCouponResponse(1L, 1L, "SUMMER2024", BigDecimal.valueOf(5000), LocalDateTime.now(), LocalDateTime.now().plusDays(30));
    }

    @PostMapping("/coupons/{couponId}/issue")
    public CouponResponse requestCouponIssue(@PathVariable Long couponId) {
        return new CouponResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @GetMapping("/users/{userId}/coupons")
    public List<UserCouponResponse> getUserCoupons(@PathVariable Long userId) {
        return Arrays.asList(
                new UserCouponResponse(1L, 1L, "SUMMER2024", BigDecimal.valueOf(5000), LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
                new UserCouponResponse(2L, 2L, "WELCOME", BigDecimal.valueOf(3000), LocalDateTime.now(), LocalDateTime.now().plusDays(15))
        );
    }

    @PostMapping("/users/{userId}/coupons/{userCouponId}/use")
    public CouponResponse useCoupon(@PathVariable Long userId, @PathVariable Long userCouponId) {
        return new CouponResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @GetMapping("/coupons/{couponId}")
    public CouponDetailResponse getCouponDetail(@PathVariable Long couponId) {
        return new CouponDetailResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000),
                100, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(30), LocalDateTime.now(), 50);
    }





}

