package com.ecommerce.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "commerce_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private BigDecimal balance;

    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    public boolean isDeleted() {
        return isDeleted;
    }

    public Optional<Long> getId() {
        return Optional.of(id);
    }

    public Optional<String> getUsername() {
        return Optional.of(username);
    }

    public Optional<BigDecimal> getBalance() {
        return Optional.of(balance);
    }

    public Optional<LocalDateTime> getDeletedAt() {
        return Optional.of(deletedAt);
    }

    public List<UserCoupon> getUserCoupons() {
        return userCoupons;
    }

    public Optional<Cart> getCart() {
        return Optional.of(cart);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setId(long id) {
        this.id = id;
    }
}