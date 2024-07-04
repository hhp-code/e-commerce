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
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponRequest request) {
        Coupon coupon = new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
        return ResponseEntity.ok(coupon);
    }

    @PostMapping("/users/{userId}/coupons")
    public ResponseEntity<UserCoupon> issueCouponToUser(@PathVariable Long userId, @RequestBody Map<String, Long> request) {
        UserCoupon userCoupon = new UserCoupon(1L, false,
                new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(30), true),
                LocalDateTime.now());
        return ResponseEntity.ok(userCoupon);
    }

    @PostMapping("/coupons/{couponId}/issue")
    public ResponseEntity<Map<String, Object>> requestCouponIssue(@PathVariable Long couponId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "쿠폰이 성공적으로 발급되었습니다.");
        response.put("userCouponId", 1L);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/coupons")
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(@PathVariable Long userId) {
        List<UserCouponResponse> response = Arrays.asList(
                new UserCouponResponse(1L, 1L, "SUMMER2024", BigDecimal.valueOf(5000), LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
                new UserCouponResponse(2L, 2L, "WELCOME", BigDecimal.valueOf(3000), LocalDateTime.now(), LocalDateTime.now().plusDays(15))
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/coupons/{userCouponId}/use")
    public ResponseEntity<Map<String, Object>> useCoupon(@PathVariable Long userId, @PathVariable Long userCouponId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "쿠폰이 성공적으로 사용되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coupons/{couponId}")
    public ResponseEntity<CouponDetailResponse> getCouponDetail(@PathVariable Long couponId) {
        CouponDetailResponse response = new CouponDetailResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000),
                100, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(30), LocalDateTime.now(), 50);
        return ResponseEntity.ok(response);
    }

    @Getter
    static class Coupon {
        private final Long id;
        private final String code;
        private final BigDecimal discountAmount;
        @Setter
        private int remainingQuantity;
        private final LocalDateTime validFrom;
        private final LocalDateTime validTo;
        private final boolean active;

        public Coupon(Long id, String code, BigDecimal discountAmount, int remainingQuantity,
                      LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
            this.id = id;
            this.code = code;
            this.discountAmount = discountAmount;
            this.remainingQuantity = remainingQuantity;
            this.validFrom = validFrom;
            this.validTo = validTo;
            this.active = active;
        }

    }


    @Getter
    static class UserCoupon {
        private final Long id;
        @Setter
        private boolean used;
        private final Coupon coupon;
        private final LocalDateTime usedAt;

        public UserCoupon(Long id, boolean used, Coupon coupon, LocalDateTime usedAt) {
            this.id = id;
            this.used = used;
            this.coupon = coupon;
            this.usedAt = usedAt;
        }

    }


    public record UserCouponResponse(Long id, Long couponId, String couponName, BigDecimal discountAmount,
                                     LocalDateTime issuedAt, LocalDateTime expiresAt) {
    }
    public record CouponRequest(String code, BigDecimal discountAmount, int remainingQuantity,
                                LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }

    public record CouponDetailResponse(Long id, String code, BigDecimal discountAmount, int quantity,
                                       int remainingQuantity, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       LocalDateTime createdAt, int issuedCount) {
    }
}