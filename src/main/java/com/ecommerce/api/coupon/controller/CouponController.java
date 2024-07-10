package com.ecommerce.api.coupon.controller;

import com.ecommerce.api.coupon.controller.dto.*;
import com.ecommerce.api.coupon.service.CouponCommand;
import com.ecommerce.api.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Tag(name = "coupon", description = "쿠폰 관련 API")
@RestController
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/coupons")
    public CouponDto.CouponResponse createCoupon(@RequestBody CouponDto.CouponRequest request) {
        return CouponMapper.toCouponResponse(
                couponService.createCoupon(CouponMapper.toCoupon(request))
        );
    }

    @PostMapping("/users/{userId}/coupons")
    public CouponDto.UserCouponResponse issueCouponToUser(@PathVariable Long userId, @RequestBody CouponDto.UserCouponRequest request) {
        return CouponMapper.toUserCouponResponse(
                couponService.issueCouponToUser(CouponMapper.toUserCouponCommand(userId, request))
        );
    }

    @PostMapping("/coupons/{couponId}/issue")
    public CouponDto.CouponResponse requestCouponIssue(@PathVariable Long couponId) {
        return new CouponDto.CouponResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @GetMapping("/users/{userId}/coupons")
    public List<CouponDto.UserCouponResponse> getUserCoupons(@PathVariable Long userId) {
        return CouponMapper.toUserCouponResponseList(couponService.getUserCoupons(userId));
    }

    @PostMapping("/users/{userId}/coupons/{userCouponId}/use")
    public CouponDto.CouponResponse useCoupon(@PathVariable Long userId, @PathVariable Long userCouponId) {
        return new CouponDto.CouponResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @GetMapping("/coupons/{couponId}")
    public CouponDto.CouponDetailResponse getCouponDetail(@PathVariable Long couponId) {
        return new CouponDto.CouponDetailResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000),
                100, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(30), LocalDateTime.now(), 50);
    }





}

