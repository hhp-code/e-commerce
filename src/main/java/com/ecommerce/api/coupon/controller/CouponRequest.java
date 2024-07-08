package com.ecommerce.api.coupon.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest(String code, BigDecimal discountAmount, int remainingQuantity,
                            LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
}
