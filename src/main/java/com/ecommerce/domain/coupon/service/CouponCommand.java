package com.ecommerce.domain.coupon.service;

import com.ecommerce.domain.coupon.DiscountType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class CouponCommand {
    public record CouponCreate(String code, BigDecimal discountAmount, int remainingQuantity, DiscountType type,
                               LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }

}
