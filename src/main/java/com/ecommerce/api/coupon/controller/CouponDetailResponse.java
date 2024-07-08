package com.ecommerce.api.coupon.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponDetailResponse(Long id, String code, BigDecimal discountAmount, int quantity,
                                   int remainingQuantity, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                   LocalDateTime createdAt, int issuedCount) {
}
