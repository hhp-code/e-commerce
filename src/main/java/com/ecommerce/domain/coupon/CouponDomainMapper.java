package com.ecommerce.domain.coupon;

import com.ecommerce.infra.coupon.entity.CouponEntity;

public class CouponDomainMapper {
    public static CouponWrite toCouponWrite(CouponEntity coupon) {
        return new CouponWrite(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountAmount(),
                coupon.getDiscountType(),
                coupon.getExpiredAt()
        );
    }

    public static CouponEntity toEntity(CouponWrite coupon) {
        return new CouponEntity(
                coupon.getCode(),
                coupon.getDiscountAmount(),
                coupon.getDiscountType(),
                coupon.getExpiredAt(),
                coupon.getQuantity()
        );
    }
}
