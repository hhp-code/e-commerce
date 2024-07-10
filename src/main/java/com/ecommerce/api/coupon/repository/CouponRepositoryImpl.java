package com.ecommerce.api.coupon.repository;

import com.ecommerce.api.coupon.service.repository.CouponRepository;
import com.ecommerce.api.domain.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJPARepository couponJPARepository;

    public CouponRepositoryImpl(CouponJPARepository couponJPARepository) {
        this.couponJPARepository = couponJPARepository;
    }


    @Override
    public Optional<Coupon> save(Coupon coupon) {
        Coupon savedCoupon = couponJPARepository.save(coupon);
        return Optional.of(savedCoupon);
    }

    @Override
    public Optional<Coupon> getById(Long couponId) {
        return couponJPARepository.findById(couponId);
    }
}
