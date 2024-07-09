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


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    public void setId(long l) {
        this.id = l;
    }
}