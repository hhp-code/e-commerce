package com.ecommerce.domain.user.service.repository;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(Long id);

    Optional<User> save(User testUser);

    Optional<Coupon> getCouponByUser(long userId, long couponId);

    Optional<BigDecimal> getAmountByUserId(Long userId);

    void saveAll(List<User> users);

    List<User> getAll();

    Optional<User> getUser(Long userId);
}
