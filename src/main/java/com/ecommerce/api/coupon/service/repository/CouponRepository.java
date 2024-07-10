package com.ecommerce.api.coupon.service.repository;

import com.ecommerce.api.domain.Coupon;

import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> save(Coupon coupon);

    Optional<Coupon> getById(Long couponId);
}
