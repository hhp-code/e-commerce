package com.ecommerce.api.coupon.controller.dto;

import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.DiscountType;
import com.ecommerce.api.domain.User;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class CouponDto {
    public record CouponDetailResponse(Long id, String code, BigDecimal discountAmount, int quantity,
                                       int remainingQuantity, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       LocalDateTime createdAt, int issuedCount) {
    }
    public record CouponRequest(String code, BigDecimal discountAmount, int remainingQuantity, DiscountType type,
                                LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }
    public record CouponResponse(Long id, String code, BigDecimal discountAmount, int remainingQuantity,
                                 LocalDateTime validFrom, LocalDateTime validTo, boolean active) {}

    public record UserCouponRequest(Long couponId) { }
    public record UserCouponResponse(Long id, boolean isUsed, LocalDateTime usedAt, User user, Coupon coupon) {
    }

}
