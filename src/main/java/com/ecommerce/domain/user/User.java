package com.ecommerce.domain.user;

import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.service.UserService;
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
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String username;

    @Getter
    private BigDecimal point;

    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @Getter
    @OneToMany(mappedBy = "user")
    public List<Coupon> coupons;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orderEntities = new ArrayList<>();


    public User() {

    }

    public User(String username, BigDecimal initialBalance) {
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = new ArrayList<>();
        this.orderEntities = new ArrayList<>();
    }
    public User(long userId, String username, BigDecimal initialBalance) {
        this.id = userId;
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = new ArrayList<>();
        this.orderEntities = new ArrayList<>();
    }
    public User(long userId, String username, BigDecimal initialBalance, List<Coupon> coupons) {
        this.id = userId;
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = coupons;
        this.orderEntities = new ArrayList<>();
    }


    public void addCoupon(Coupon coupon) {
        coupons.add(coupon);
    }

    public Coupon getCoupon(long couponId) {
        return coupons.stream()
                .filter(coupon -> coupon.getId().equals(couponId))
                .findFirst()
                .orElseThrow(() -> new UserException("사용자에게 발급된 쿠폰을 찾을 수 없습니다."));
    }

    public void addOrder(OrderEntity orderEntity) {
        orderEntities.add(orderEntity);
    }

    public User chargePoint(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("충전 금액은 0보다 커야 합니다.");
        }
        this.point = this.point.add(amount);
        return this;
    }

    public User deductPoint(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("차감 금액은 0보다 커야 합니다.") {
            };
        }
        if (this.point.compareTo(amount) < 0) {
            throw new UserException("잔액이 부족합니다.");
        }
        this.point = this.point.subtract(amount);
        return this;
    }


    public User saveAndGet(UserService userService) {
        return userService.save(this);
    }
}