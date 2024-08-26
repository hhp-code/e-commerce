package com.ecommerce.domain.user;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.interfaces.exception.domain.UserException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Getter
public class User {
    private long id;
    private final String username;
    private BigDecimal initialBalance;
    private final boolean isDeleted;

    private List<CouponWrite> coupons = new ArrayList<>();

    public User(String username, BigDecimal initialBalance) {
        this.username = username;
        this.initialBalance = initialBalance;
        this.isDeleted = false;
    }

    public User(String test, Object initialBalance, List<CouponWrite> testCoupon) {
        this.username = test;
        this.initialBalance = (BigDecimal) initialBalance;
        this.coupons = testCoupon;
        this.isDeleted = false;
    }

    public User(long l, String test, BigDecimal zero, List<CouponWrite> testCoupon) {
        this.id = l;
        this.username = test;
        this.initialBalance = zero;
        this.coupons = testCoupon;
        this.isDeleted = false;
    }



    public User chargePoint(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("충전 금액은 0보다 커야 합니다.");
        }
        this.initialBalance = this.initialBalance.add(amount);
        return this;
    }

    public User deductPoint(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("차감 금액은 0보다 커야 합니다.");
        }
        if (this.initialBalance.compareTo(amount) < 0) {
            throw new UserException("잔액이 부족합니다.");
        }
        this.initialBalance = this.initialBalance.subtract(amount);
        return this;
    }

    public void addCoupon(CouponWrite coupon) {
        coupons.add(coupon);
    }

    public BigDecimal getPoint() {
        return initialBalance;
    }

}