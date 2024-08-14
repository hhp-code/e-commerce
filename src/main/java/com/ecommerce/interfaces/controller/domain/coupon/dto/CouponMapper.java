package com.ecommerce.interfaces.controller.domain.coupon.dto;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.coupon.service.CouponCommand;

import java.time.Instant;

public class CouponMapper {
    public static CouponCommand.Create toCoupon(CouponDto.CouponRequest request) {
        return new CouponCommand.Create(request.code(), request.discountAmount(), request.remainingQuantity(),request.type(),
                request.validFrom(), request.validTo(), request.active());
    }

    public static CouponDto.CouponResponse toCouponResponse(CouponWrite coupon) {
        return new CouponDto.CouponResponse( coupon.getCode(), coupon.getDiscountAmount(),
                coupon.getValidFrom(), coupon.getValidTo(), coupon.getActive());

    }


    public static CouponDto.CouponDetailResponse toCouponDetailResponse(CouponWrite coupon) {
        return new CouponDto.CouponDetailResponse( coupon.getCode(), coupon.getDiscountAmount(), coupon.getQuantity(),
                coupon.getValidFrom(), coupon.getValidTo(), coupon.getActive());
    }

    public static CouponCommand.Issue toUserCouponCommand(Long userId, Long couponId) {
        return new CouponCommand.Issue(userId, couponId, Instant.now());
    }

}
