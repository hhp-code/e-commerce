package com.ecommerce.domain.coupon.service.repository;

import com.ecommerce.domain.coupon.Coupon;

import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> save(Coupon coupon);

    Optional<Coupon> getById(Long couponId);

    int getRemainingQuantity(Long couponId);

    void deleteAll();
}
