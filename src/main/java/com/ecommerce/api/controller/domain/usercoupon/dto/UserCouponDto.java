package com.ecommerce.api.controller.domain.usercoupon.dto;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class UserCouponDto {
    public record UserCouponRequest(Long couponId) { }
    public record UserCouponResponse(Long id, boolean isUsed, LocalDateTime usedAt, User user, Coupon coupon) {
    }

}
