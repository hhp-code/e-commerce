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

    Optional<User> getUserByCoupon(Coupon userCoupon);

    void deleteAll();

    boolean hasCoupon(Long aLong, Long aLong1);

    void saveAll(List<User> users);


    Optional<User> getUserWithCoupon(Long userId);

    List<User> getAll();
    Optional<BigDecimal> getAmountByUserId(Long userId);

    Optional<User> saveChargeAmount(Long userId, BigDecimal amount);

    Optional<User> getUserByRequest(Long userId);

    Optional<BigDecimal> getAmountByUserIdWithLock(long userId);

    Optional<User> saveDeductAmount(long userId, BigDecimal totalAmount);
}
