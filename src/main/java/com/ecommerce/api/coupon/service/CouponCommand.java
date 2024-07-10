package com.ecommerce.api.coupon.service;

import com.ecommerce.api.coupon.controller.dto.CouponDto;
import com.ecommerce.api.domain.DiscountType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class CouponCommand {
    public record CouponCreate(String code, BigDecimal discountAmount, int remainingQuantity, DiscountType type,
                               LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }

    public record UserCouponCreate(Long userId, CouponDto.UserCouponRequest request) {
    }
}
