package com.ecommerce.domain.coupon.service;

import com.ecommerce.domain.coupon.service.repository.CouponRepository;
import com.ecommerce.domain.coupon.Coupon;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public Coupon createCoupon(CouponCommand.Create create) {
        Coupon coupon = new Coupon(create.code(),
                create.discountAmount(),
                create.type(),
                create.remainingQuantity(),
                create.validFrom(),
                create.validTo(), true);
        return couponRepository.save(coupon).orElseThrow(
                () -> new RuntimeException("쿠폰을 생성할 수 없습니다.")
        );
    }

    public Coupon getCoupon(Long couponId) {
        return couponRepository.getById(couponId).orElseThrow(
                () -> new RuntimeException("쿠폰을 찾을 수 없습니다.")
        );
    }

    public void updateCoupon(Coupon coupon) {
        couponRepository.save(coupon).orElseThrow(
                () -> new RuntimeException("쿠폰을 업데이트할 수 없습니다.")
        );
    }

    public Coupon decrementCouponQuantity(Long couponId) {
        Coupon coupon = getCoupon(couponId);
        if (coupon.decrementQuantity()) {
            throw new IllegalStateException("쿠폰 수량이 부족합니다.");
        }
        return coupon;
    }

    public void save(Coupon coupon) {
        couponRepository.save(coupon).orElseThrow(
                () -> new RuntimeException("쿠폰을 저장할 수 없습니다.")
        );
    }

    public int getRemainingQunatity(Long couponId) {
        return couponRepository.getRemainingQuantity(couponId);
    }
}