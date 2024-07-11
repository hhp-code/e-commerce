package com.ecommerce.domain.user;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.usercoupon.UserCoupon;
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
    private BigDecimal balance;

    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    public User() {

    }

    public User(String username, BigDecimal initialBalance) {
        this.username = username;
        this.balance = initialBalance;
        this.isDeleted = false;
        this.userCoupons = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    public User(long userId, String username, BigDecimal initialBalance) {
        this.id = userId;
        this.username = username;
        this.balance = initialBalance;
        this.isDeleted = false;
        this.userCoupons = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public boolean isDeleted() {
        return isDeleted;
    }


    public void addCoupon(Coupon coupon) {
        UserCoupon userCoupon = new UserCoupon(this, coupon);
        userCoupons.add(userCoupon);
    }

    public UserCoupon getCoupon(long couponId) {
        return userCoupons.stream().filter(UserCoupon::isUsed).findFirst().orElseThrow(
                () -> new RuntimeException("사용 가능한 쿠폰이 없습니다.")
        );
    }

    public void addOrder(Order order) {
        orders.add(order);
    }
}