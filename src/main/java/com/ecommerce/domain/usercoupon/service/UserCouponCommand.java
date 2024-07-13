package com.ecommerce.domain.usercoupon.service;

import com.ecommerce.api.controller.domain.usercoupon.dto.UserCouponDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserCouponCommand {
    public record UserCouponCreate(Long userId, UserCouponDto.UserCouponRequest request) {
    }
}
