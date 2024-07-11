package com.ecommerce.api.controller.domain.coupon.dto;

import com.ecommerce.domain.coupon.DiscountType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class CouponDto {
    public record CouponDetailResponse(Long id, String code, BigDecimal discountAmount, int quantity,
                                     LocalDateTime startDateTime, LocalDateTime endDateTime,
                                        boolean active) {
    }
    public record CouponRequest(String code, BigDecimal discountAmount, int remainingQuantity, DiscountType type,
                                LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }
    public record CouponResponse(Long id, String code, BigDecimal discountAmount, int remainingQuantity,
                                 LocalDateTime validFrom, LocalDateTime validTo, boolean active) {}



}
