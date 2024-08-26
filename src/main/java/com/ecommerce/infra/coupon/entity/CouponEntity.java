package com.ecommerce.infra.coupon.entity;

import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.infra.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class CouponEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String code;

    @Getter
    private BigDecimal discountAmount;

    @Getter
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Getter
    private Integer quantity;

    @Getter
    private LocalDateTime validFrom;
    @Getter
    private LocalDateTime validTo;
    @Getter
    private boolean isActive;

    public CouponEntity(String code, BigDecimal bigDecimal, DiscountType type, int quantity, LocalDateTime localDateTime, LocalDateTime localDateTime1, boolean b) {
        this.code = code;
        this.discountAmount = bigDecimal;
        this.discountType = type;
        this.quantity = quantity;
        this.validFrom = localDateTime;
        this.validTo = localDateTime1;
        this.isActive = b;
    }

    public CouponEntity() {

    }

    public CouponEntity(String code, BigDecimal discountAmount, DiscountType discountType, LocalDateTime expiredAt, Integer quantity) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.quantity = quantity;
        this.validFrom = LocalDateTime.now();
        this.validTo = LocalDateTime.now().plusDays(7);
        this.isActive = true;
    }


    public boolean deductQuantity() {
        if (quantity <= 0) {
            return false;
        }
        quantity = quantity - 1;
        return true;
    }

    public Object getExpiredAt() {
        return validTo;
    }
}