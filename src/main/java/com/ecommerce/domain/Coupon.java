package com.ecommerce.domain;

import com.ecommerce.domain.UserCoupon;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private BigDecimal discountAmount;
    private Integer remainingQuantity;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isActive;

    @OneToMany(mappedBy = "coupon")
    private List<UserCoupon> userCoupons;

    // Getters and setters
}