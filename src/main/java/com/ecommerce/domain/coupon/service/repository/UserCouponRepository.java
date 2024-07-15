package com.ecommerce.domain.coupon.service.repository;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {

    List<Coupon> getAllCouponsByUserId(Long userId);

    Optional<Coupon> getCouponByUser(User user, Coupon coupon);
}
