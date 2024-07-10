package com.ecommerce.domain;

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

    private String username;
    @Setter
    @Getter
    private BigDecimal balance;

    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons;

    @OneToOne(mappedBy = "user" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    public User(String username, BigDecimal initialBalance) {
        this.username = username;
        this.balance = initialBalance;
        this.isDeleted = false;
        this.userCoupons = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public User() {

    }


    public boolean isDeleted() {
        return isDeleted;
    }




    public void setId(long l) {
        this.id = l;
    }
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }
    public Cart getCart(){
        if (this.cart == null) {
            this.cart = new Cart(this);
        }
        return this.cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
        cart.setUser(this);
    }

}