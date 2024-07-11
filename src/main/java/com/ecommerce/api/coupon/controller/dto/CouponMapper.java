package com.ecommerce.api.coupon.controller.dto;

import com.ecommerce.api.coupon.service.CouponCommand;
import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.UserCoupon;

import java.util.ArrayList;
import java.util.List;

public class CouponMapper {
    public static CouponCommand.CouponCreate toCoupon(CouponDto.CouponRequest request) {
        return new CouponCommand.CouponCreate(request.code(), request.discountAmount(), request.remainingQuantity(),request.type(),
                request.validFrom(), request.validTo(), request.active());
    }

    public static CouponDto.CouponResponse toCouponResponse(Coupon coupon) {
        return new CouponDto.CouponResponse(coupon.getId(), coupon.getCode(), coupon.getDiscountAmount(), coupon.getQuantity(),
                coupon.getValidFrom(), coupon.getValidTo(), coupon.getActive());

    }

    public static CouponDto.UserCouponResponse toUserCouponResponse(UserCoupon userCoupon){
        return new CouponDto.UserCouponResponse(userCoupon.getId(), userCoupon.isUsed(), userCoupon.getUsedAt(), userCoupon.getUser(), userCoupon.getCoupon());
    }
    public static CouponDto.CouponDetailResponse toCouponDetailResponse(Coupon coupon) {
        return new CouponDto.CouponDetailResponse(coupon.getId(), coupon.getCode(), coupon.getDiscountAmount(), coupon.getQuantity(),
                coupon.getValidFrom(), coupon.getValidTo(), coupon.getActive());
    }

    public static CouponCommand.UserCouponCreate toUserCouponCommand(Long userId, CouponDto.UserCouponRequest request) {
        return new CouponCommand.UserCouponCreate(userId, request);
    }

    public static List<CouponDto.UserCouponResponse> toUserCouponResponseList(List<UserCoupon> userCoupons) {
        List<CouponDto.UserCouponResponse> convertedResponse = new ArrayList<>();
        for(UserCoupon coupon : userCoupons){
            convertedResponse.add(new CouponDto.UserCouponResponse(coupon.getId(), coupon.isUsed(), coupon.getUsedAt(), coupon.getUser(), coupon.getCoupon()));
        }
        return convertedResponse;
    }
}
