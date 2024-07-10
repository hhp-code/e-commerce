package com.ecommerce.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isUsed;
    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    public UserCoupon() {
    }

    public UserCoupon(User user, Coupon coupon) {
        this.user = user;
        this.coupon = coupon;
        this.isUsed = false;
    }

    public void use() {
        if (!isUsed) {
            this.isUsed = true;
            this.usedAt = LocalDateTime.now();
        }
    }

    public void cancelUse() {
        if (isUsed) {
            this.isUsed = false;
            this.usedAt = null;
        }
    }
}