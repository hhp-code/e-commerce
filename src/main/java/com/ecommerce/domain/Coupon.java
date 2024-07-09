package com.ecommerce.domain;

import com.ecommerce.domain.UserCoupon;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    @Getter
    private BigDecimal discountAmount;
    @Getter
    private DiscountType discountType;
    private Integer remainingQuantity;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isActive;

    @OneToMany(mappedBy = "coupon")
    private List<UserCoupon> userCoupons;

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && remainingQuantity > 0 && now.isAfter(validFrom) && now.isBefore(validTo);
    }

    // Getters and setters
}