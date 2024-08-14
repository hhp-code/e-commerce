package com.ecommerce.domain.user.service.repository;

import com.ecommerce.infra.coupon.entity.CouponEntity;
import com.ecommerce.infra.user.entity.UserEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> getById(Long id);

    Optional<UserEntity> save(UserEntity testUser);

    Optional<CouponEntity> getCouponByUser(long userId, long couponId);

    Optional<BigDecimal> getAmountByUserId(Long userId);

    void saveAll(List<UserEntity> users);

    List<UserEntity> getAll();

    Optional<UserEntity> getUser(Long userId);
}
