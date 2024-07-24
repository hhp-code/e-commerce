package com.ecommerce.domain.coupon.service;

import com.ecommerce.domain.coupon.DiscountType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@UtilityClass
public class CouponCommand {
    public record Create(String code, BigDecimal discountAmount, int quantity, DiscountType type,
                         LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }
    public record Issue(Long userId,Long couponId, Instant issuedAt) implements Comparable<Issue> {
        @Override
        public int compareTo(Issue o) {
            return issuedAt.compareTo(o.issuedAt);
        }


    }

}
