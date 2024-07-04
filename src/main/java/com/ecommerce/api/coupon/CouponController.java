package com.ecommerce.api.coupon;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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



    public record UserCouponRequest(Long couponId) { }

    public record UserCouponResponse(Long id, Long couponId, String couponName, BigDecimal discountAmount,
                                     LocalDateTime issuedAt, LocalDateTime expiresAt) {
    }
    public record CouponRequest(String code, BigDecimal discountAmount, int remainingQuantity,
                                LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }
    public record CouponResponse(Long id, String code, BigDecimal discountAmount, int remainingQuantity,
                                 LocalDateTime validFrom, LocalDateTime validTo, boolean active) {}

    public record CouponDetailResponse(Long id, String code, BigDecimal discountAmount, int quantity,
                                       int remainingQuantity, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       LocalDateTime createdAt, int issuedCount) {
    }
}