package com.ecommerce.api.coupon.repository;

import com.ecommerce.api.coupon.service.repository.UserCouponRepository;
import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.User;
import com.ecommerce.api.domain.UserCoupon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJPARepository userCouponJPARepository;

    public UserCouponRepositoryImpl(UserCouponJPARepository userCouponJPARepository) {
        this.userCouponJPARepository = userCouponJPARepository;
    }

    @Override
    public Optional<UserCoupon> getCouponByUser(User user, Coupon coupon) {
        UserCoupon byUserAndCoupon = userCouponJPARepository.findByUserAndCoupon(user, coupon);
        return Optional.of(byUserAndCoupon);
    }

    @Override
    public List<UserCoupon> getAllCouponsByUserId(Long userId) {
        return userCouponJPARepository.findAllByUserId(userId);
    }

    @Override
    public Optional<UserCoupon> save(UserCoupon userCoupon) {
       return Optional.of(userCouponJPARepository.save(userCoupon));
    }
}
