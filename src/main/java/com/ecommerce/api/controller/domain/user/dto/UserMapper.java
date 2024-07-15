package com.ecommerce.api.controller.domain.user.dto;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto.UserResponse toUserResponse(User user) {
        return new UserDto.UserResponse(user.getUsername(), user.getBalance(), user.getCoupons());
    }
    public static List<UserDto.CouponResponse> toUserCouponResponseList(List<Coupon> userCoupons) {
        List<UserDto.CouponResponse> convertedResponse = new ArrayList<>();
        for(Coupon coupon : userCoupons){
            convertedResponse.add(new UserDto.CouponResponse(coupon.getCode(), coupon.getDiscountAmount(), coupon.getValidFrom(), coupon.getValidTo(),coupon.isValid()));
        }
        return convertedResponse;
    }
}
