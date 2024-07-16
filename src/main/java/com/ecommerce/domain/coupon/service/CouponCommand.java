package com.ecommerce.domain.coupon.service;

import com.ecommerce.domain.coupon.DiscountType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@UtilityClass
public class CouponCommand {
    public record Create(String code, BigDecimal discountAmount, int remainingQuantity, DiscountType type,
                         LocalDateTime validFrom, LocalDateTime validTo, boolean active) {
    }
    public record Issue(Long couponId, Long userId,Status status,  Instant timeStamp) implements Comparable<Issue> {
        public enum Status{
            PENDING, PROCESSING, COMPLETED, FAILED
        }
        @Override
        public int compareTo(Issue o) {
            return this.timeStamp.compareTo(o.timeStamp);
        }
    }

}
