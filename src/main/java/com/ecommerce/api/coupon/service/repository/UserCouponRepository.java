package com.ecommerce.api.coupon.service.repository;

import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.User;
import com.ecommerce.api.domain.UserCoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> getCouponByUser(User user, Coupon coupon);

    List<UserCoupon> getAllCouponsByUserId(Long userId);

    Optional<UserCoupon> save(UserCoupon userCoupon);
}
