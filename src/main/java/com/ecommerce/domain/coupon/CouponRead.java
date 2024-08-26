package com.ecommerce.domain.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponRead {
    private Long id;
    private String code;
    private BigDecimal discountAmount;
    private DiscountType discountType;
    private Integer quantity;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isActive;


    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && quantity > 0 && now.isAfter(validFrom) && now.isBefore(validTo);
    }
}