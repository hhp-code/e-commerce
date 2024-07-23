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

    List<Coupon> getAllCouponsByUserId(Long userId);

    void deleteAll();

    Optional<BigDecimal> getAmountByUserId(Long userId);

    Optional<User> saveChargeAmount(Long userId, BigDecimal amount);

    Optional<User> getUserByRequest(Long userId);

    Optional<BigDecimal> getAmountByUserIdWithLock(Long userId);

    Optional<User> getByIdWithLock(Long userId);

    void saveAll(List<User> users);
}
