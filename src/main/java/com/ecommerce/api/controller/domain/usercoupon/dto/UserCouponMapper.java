package com.ecommerce.api.controller.domain.usercoupon.dto;

import com.ecommerce.domain.usercoupon.UserCoupon;
import com.ecommerce.domain.usercoupon.service.UserCouponCommand;

import java.util.ArrayList;
import java.util.List;

public class UserCouponMapper {
    public static UserCouponDto.UserCouponResponse toUserCouponResponse(UserCoupon userCoupon){
        return new UserCouponDto.UserCouponResponse(userCoupon.getId(), userCoupon.isUsed(), userCoupon.getUsedAt(), userCoupon.getUser(), userCoupon.getCoupon());
    }
    public static UserCouponCommand.UserCouponCreate toUserCouponCommand(Long userId, UserCouponDto.UserCouponRequest request) {
        return new UserCouponCommand.UserCouponCreate(userId, request);
    }

    public static List<UserCouponDto.UserCouponResponse> toUserCouponResponseList(List<UserCoupon> userCoupons) {
        List<UserCouponDto.UserCouponResponse> convertedResponse = new ArrayList<>();
        for(UserCoupon coupon : userCoupons){
            convertedResponse.add(new UserCouponDto.UserCouponResponse(coupon.getId(), coupon.isUsed(), coupon.getUsedAt(), coupon.getUser(), coupon.getCoupon()));
        }
        return convertedResponse;
    }
}
