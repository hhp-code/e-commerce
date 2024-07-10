package com.ecommerce.api.domain;

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

    @Setter
    @OneToOne(mappedBy = "user" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

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
        this.cart= new Cart();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Cart getCart(){
        if (this.cart == null) {
            this.cart = new Cart();
        }
        return this.cart;
    }

    public void addCoupon(Coupon coupon) {
        UserCoupon userCoupon = new UserCoupon(this, coupon);
        userCoupons.add(userCoupon);
    }
}