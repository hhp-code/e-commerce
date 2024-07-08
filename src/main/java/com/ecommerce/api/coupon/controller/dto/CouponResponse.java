package com.ecommerce.api.coupon.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(Long id, String code, BigDecimal discountAmount, int remainingQuantity,
                             LocalDateTime validFrom, LocalDateTime validTo, boolean active) {}
