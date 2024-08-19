package com.ecommerce.domain.user;

import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.domain.order.Order;
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


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    public User() {

    }

    public User(String username, BigDecimal initialBalance) {
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.orders = new ArrayList<>();
    }
    public User(long userId, String username, BigDecimal initialBalance) {
        this.id = userId;
        this.username = username;
        this.point = initialBalance;
        this.isDeleted = false;
        this.orders = new ArrayList<>();
    }




    public void addOrder(Order order) {
        orders.add(order);
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