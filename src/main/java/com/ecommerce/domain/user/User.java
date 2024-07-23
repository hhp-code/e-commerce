package com.ecommerce.domain.user;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commerce_user")
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String username;

    @Setter
    @Getter
    private BigDecimal point;

    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @Getter
    @OneToMany(mappedBy = "user")
    public List<Coupon> coupons;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    public User() {

    }

    public User(String username, BigDecimal initialBalance) {
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    public User(long userId, String username, BigDecimal initialBalance) {
        this.id = userId;
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    public User(long userId, String username, BigDecimal initialBalance, List<Coupon> coupons) {
        this.id = userId;
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.coupons = coupons;
        this.orders = new ArrayList<>();
    }


    public void addCoupon(Coupon coupon) {
        coupons.add(coupon);
    }

    public Coupon getCoupon(long couponId) {
        System.out.println(couponId + "wow");
        return coupons.stream()
                .filter(coupon -> coupon.getId().equals(couponId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("사용자에게 발급된 쿠폰을 찾을 수 없습니다."));
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

}