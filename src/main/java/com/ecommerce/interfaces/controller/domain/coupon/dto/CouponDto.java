package com.ecommerce.interfaces.controller.domain.coupon.dto;

import com.ecommerce.domain.coupon.DiscountType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class CouponDto {
    public record CouponDetailResponse(String code, BigDecimal discountAmount, int quantity,
                                       LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       boolean active) {
    }
    public record CouponRequest(String code, BigDecimal discountAmount, int remainingQuantity, DiscountType type,
                                LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
        public void validate() {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("코드는 필수입니다.");
            }
            if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("할인 금액은 0보다 커야 합니다.");
            }
            if (remainingQuantity < 0) {
                throw new IllegalArgumentException("남은 수량은 0보다 작을 수 없습니다.");
            }
            if (validFrom == null || validTo == null) {
                throw new IllegalArgumentException("유효 기간은 필수입니다.");
            }
            if (validFrom.isAfter(validTo)) {
                throw new IllegalArgumentException("유효 기간이 올바르지 않습니다.");
            }
        }
    }
    public record CouponResponse(String code, BigDecimal discountAmount,
                                 LocalDateTime validFrom, LocalDateTime validTo, boolean active) {}



}
