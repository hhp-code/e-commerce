package com.ecommerce.api.coupon.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserCouponResponse(Long id, Long couponId, String couponName, BigDecimal discountAmount,
                                 LocalDateTime issuedAt, LocalDateTime expiresAt) {
}
