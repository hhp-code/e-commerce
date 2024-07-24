package com.ecommerce.domain.coupon.service;

import com.ecommerce.api.exception.domain.CouponException;
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
                create.quantity(),
                create.validFrom(),
                create.validTo(), true);
        return couponRepository.save(coupon).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 생성할 수 없습니다.")
        );
    }

    @Transactional(readOnly = true)
    public Coupon getCoupon(Long couponId) {
        return couponRepository.getById(couponId).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 찾을 수 없습니다.")
        );
    }

    @Transactional
    public Coupon deductCoupon(Long couponId) {
        Coupon coupon = getCoupon(couponId);
        if (coupon.deductQuantity()) {
            throw new CouponException.ServiceException("쿠폰 수량이 부족합니다.");
        }
        return coupon;
    }

    @Transactional
    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 저장할 수 없습니다.")
        );
    }

    @Transactional
    public int getRemainingQuantity(Long currentCouponId) {
        return couponRepository.getById(currentCouponId).map(Coupon::getQuantity).orElse(0);
    }

    public void deleteAll() {
        couponRepository.deleteAll();
    }

    public void save(Coupon coupon) {
        couponRepository.save(coupon);
    }

    public Coupon saveAndGet(Coupon coupon) {
        return couponRepository.save(coupon).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰 저장에 실패했습니다.")
        );
    }
}