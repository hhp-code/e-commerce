package com.ecommerce.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class UserCoupon {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isUsed;
    @Getter
    private LocalDateTime usedAt;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
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

    public boolean isUsed() {
        return isUsed;
    }


}