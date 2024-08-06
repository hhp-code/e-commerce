package com.ecommerce.domain.coupon;

import com.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    private Integer quantity;

    @Getter
    private LocalDateTime validFrom;
    @Getter
    private LocalDateTime validTo;
    private boolean isActive;

    public Coupon() {
    }

    public Coupon(String code, BigDecimal discountAmount, DiscountType discountType,
                  Integer quantity) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.quantity = quantity;
        this.validFrom = LocalDateTime.now();
        this.validTo = LocalDateTime.now().plusDays(7);
        this.isActive = true;
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
    public Coupon(long couponId, String code, BigDecimal discountAmount, DiscountType discountType,
                  Integer quantity, LocalDateTime validFrom, LocalDateTime validTo,
                  boolean isActive) {
        this.id = couponId;
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
    public boolean deductQuantity() {
        if (quantity <= 0) {
            return false;
        }
        quantity = quantity -1;
        return true;
    }

    public boolean getActive() {
        return isActive;
    }


    public void use() {
        this.isActive = false;
    }

}