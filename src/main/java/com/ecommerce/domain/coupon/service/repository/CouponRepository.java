package com.ecommerce.domain.coupon.service.repository;

import com.ecommerce.infra.coupon.entity.CouponEntity;

import java.util.Optional;

public interface CouponRepository {

    Optional<CouponEntity> save(CouponEntity coupon);

    Optional<CouponEntity> getById(Long couponId);

    int getStock(Long couponId);

    void deleteAll();
}
