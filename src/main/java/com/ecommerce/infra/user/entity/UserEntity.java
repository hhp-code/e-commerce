package com.ecommerce.infra.user.entity;


import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.infra.coupon.entity.CouponEntity;
import com.ecommerce.infra.order.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
})
public class UserEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String username;
    private BigDecimal point;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user")
    public List<CouponEntity> coupons;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orderEntities = new ArrayList<>();

    public UserEntity(String username, BigDecimal initialBalance) {
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = new ArrayList<>();
        this.orderEntities = new ArrayList<>();
    }

    public UserEntity() {

    }

    public UserEntity(Long userId, String testUser, BigDecimal zero, List<CouponWrite> testCoupons) {
        this.id = userId;
        this.username = testUser;
        this.point = zero;
        this.coupons = new ArrayList<>();

    }

    public BigDecimal getInitialBalance() {
        return point;
    }

}