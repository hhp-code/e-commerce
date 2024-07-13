package com.ecommerce.domain.coupon.service.repository;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.usercoupon.UserCoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> getCouponByUser(User user, Coupon coupon);

    List<UserCoupon> getAllCouponsByUserId(Long userId);

    Optional<UserCoupon> save(UserCoupon userCoupon);
}
