package com.ecommerce.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Coupon {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String code;
    @Getter
    private BigDecimal discountAmount;
    @Getter
    private DiscountType discountType;

    private Integer quantity;
    @Getter
    private LocalDateTime validFrom;
    @Getter
    private LocalDateTime validTo;
    private boolean isActive;

    public Coupon() {
    }

    public Coupon(String code, BigDecimal discountAmount, DiscountType discountType,
                  Integer quantity, LocalDateTime validFrom, LocalDateTime validTo,
                  boolean isActive) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.quantity = quantity;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isActive = isActive;
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && quantity > 0 && now.isAfter(validFrom) && now.isBefore(validTo);
    }

    public boolean getActive() {
        return isActive;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int i) {
        this.quantity = i;
    }
}