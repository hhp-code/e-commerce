package com.ecommerce.api.controller.domain.coupon.dto;

import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.Coupon;

public class CouponMapper {
    public static CouponCommand.CouponCreate toCoupon(CouponDto.CouponRequest request) {
        return new CouponCommand.CouponCreate(request.code(), request.discountAmount(), request.remainingQuantity(),request.type(),
                request.validFrom(), request.validTo(), request.active());
    }

    public static CouponDto.CouponResponse toCouponResponse(Coupon coupon) {
        return new CouponDto.CouponResponse(coupon.getId(), coupon.getCode(), coupon.getDiscountAmount(), coupon.getQuantity(),
                coupon.getValidFrom(), coupon.getValidTo(), coupon.getActive());

    }


    public static CouponDto.CouponDetailResponse toCouponDetailResponse(Coupon coupon) {
        return new CouponDto.CouponDetailResponse(coupon.getId(), coupon.getCode(), coupon.getDiscountAmount(), coupon.getQuantity(),
                coupon.getValidFrom(), coupon.getValidTo(), coupon.getActive());
    }


}
