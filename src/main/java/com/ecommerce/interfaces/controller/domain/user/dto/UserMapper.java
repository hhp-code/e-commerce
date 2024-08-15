package com.ecommerce.interfaces.controller.domain.user.dto;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.command.UserCommand;
import com.ecommerce.domain.user.UserWrite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto.UserResponse toUserResponse(UserWrite user) {
        return new UserDto.UserResponse(user.getUsername(), user.getPoint(), user.getCoupons());
    }

    public static List<UserDto.CouponResponse> toUserCouponResponseList(List<CouponWrite> userCoupons) {
        List<UserDto.CouponResponse> convertedResponse = new ArrayList<>();
        for(CouponWrite coupon : userCoupons){
            convertedResponse.add(new UserDto.CouponResponse(coupon.getCode(), coupon.getDiscountAmount(), coupon.getValidFrom(), coupon.getValidTo(),coupon.isValid()));
        }
        return convertedResponse;
    }
    public static UserCommand.Charge toCharge(Long userId, BigDecimal amount){
        return new UserCommand.Charge(userId, amount);
    }

}
