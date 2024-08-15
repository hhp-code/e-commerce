package com.ecommerce.domain.coupon;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class CouponWrite{
    private Long id;
    private String code;
    private BigDecimal discountAmount;
    private DiscountType discountType;
    private Integer quantity;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isActive;

    public CouponWrite(String code, BigDecimal discountAmount, DiscountType discountType, Object expiredAt) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.quantity = 1;
        this.validFrom = LocalDateTime.now();
        this.validTo = LocalDateTime.now().plusDays(7);
        this.isActive = true;
    }

    public CouponWrite(Long id, String code, BigDecimal discountAmount, DiscountType discountType, Object expiredAt) {
        this.id = id;
        this.code = code;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.quantity = 1;
        this.validFrom = LocalDateTime.now();
        this.validTo = LocalDateTime.now().plusDays(7);
        this.isActive = true;
    }

    public CouponWrite(long l, String summer2024, BigDecimal bigDecimal, DiscountType discountType, int i, LocalDateTime now, LocalDateTime localDateTime, boolean b) {

    }

    public CouponWrite() {

    }

    public CouponWrite(String test10, BigDecimal bigDecimal, DiscountType discountType, int i, LocalDateTime localDateTime, LocalDateTime localDateTime1, boolean b) {

    }


    public boolean deductQuantity() {
        if (quantity <= 0) {
            return false;
        }
        quantity = quantity - 1;
        return true;
    }

    public void use() {
        this.isActive = false;
    }


    public boolean isValid() {
        return isActive;
    }


    public boolean getActive() {
        return isActive;
    }

    public LocalDateTime getExpiredAt() {
        return validTo;
    }
}