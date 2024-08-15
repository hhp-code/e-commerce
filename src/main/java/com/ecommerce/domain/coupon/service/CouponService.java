package com.ecommerce.domain.coupon.service;

import com.ecommerce.domain.coupon.CouponDomainMapper;
import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.infra.coupon.entity.CouponEntity;
import com.ecommerce.interfaces.exception.domain.CouponException;
import com.ecommerce.domain.coupon.service.repository.CouponRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponWrite createCoupon(CouponCommand.Create create) {
        CouponEntity coupon = new CouponEntity(create.code(),
                create.discountAmount(),
                create.type(),
                create.quantity(),
                create.validFrom(),
                create.validTo(), true);
        CouponEntity coupon1 = couponRepository.save(coupon).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 생성할 수 없습니다.")
        );
        return CouponDomainMapper.toCouponWrite(coupon1);
    }

    @Transactional(readOnly = true)
    public CouponWrite getCoupon(Long couponId) {
        CouponEntity couponEntity = couponRepository.getById(couponId).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 찾을 수 없습니다.")
        );
        return CouponDomainMapper.toCouponWrite(couponEntity);
    }

    @Transactional
    public CouponWrite deductCoupon(Long couponId) {
        CouponEntity coupon = couponRepository.getById(couponId).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 찾을 수 없습니다.")
        );
        if (coupon.deductQuantity()) {
            throw new CouponException.ServiceException("쿠폰 수량이 부족합니다.");
        }
        return CouponDomainMapper.toCouponWrite(coupon);
    }

    @Transactional
    public void saveCoupon(CouponWrite coupon) {
        CouponEntity entity = CouponDomainMapper.toEntity(coupon);
        couponRepository.save(entity).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰을 저장할 수 없습니다.")
        );
    }

    @Transactional
    public int getStock(Long currentCouponId) {
        return couponRepository.getStock(currentCouponId);
    }

    public void deleteAll() {
        couponRepository.deleteAll();
    }

    public void save(CouponEntity coupon) {
        couponRepository.save(coupon);
    }

    public CouponWrite saveAndGet(CouponWrite coupon) {
        CouponEntity entity = CouponDomainMapper.toEntity(coupon);
        CouponEntity couponEntity = couponRepository.save(entity).orElseThrow(
                () -> new CouponException.ServiceException("쿠폰 저장에 실패했습니다.")
        );
        return CouponDomainMapper.toCouponWrite(couponEntity);
    }
}