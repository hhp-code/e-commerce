package com.ecommerce.infra.coupon;

import com.ecommerce.domain.coupon.service.repository.CouponRepository;
import com.ecommerce.infra.coupon.entity.CouponEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJPARepository couponJPARepository;

    public CouponRepositoryImpl(CouponJPARepository couponJPARepository) {
        this.couponJPARepository = couponJPARepository;
    }



    @Override
    public Optional<CouponEntity> save(CouponEntity coupon) {
        CouponEntity savedCoupon = couponJPARepository.save(coupon);
        return Optional.of(savedCoupon);
    }

    @Override
    public Optional<CouponEntity> getById(Long couponId) {
        return couponJPARepository.findById(couponId);
    }

    @Override
    public int getStock(Long couponId) {
        CouponEntity coupon = couponJPARepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("쿠폰을 찾을 수 없습니다: " + couponId));
        return coupon.getQuantity();

    }

    @Override
    public void deleteAll() {
        couponJPARepository.deleteAll();
    }
}
