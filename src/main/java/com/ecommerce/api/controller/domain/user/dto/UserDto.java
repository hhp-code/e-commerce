package com.ecommerce.api.controller.domain.user.dto;

import com.ecommerce.domain.coupon.Coupon;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@UtilityClass
public class UserDto {
    public record UserBalanceResponse(boolean success, String message, Map<String, Object> data) {
    }
    public record UserResponse(String username, BigDecimal balance, List<Coupon> coupons) {
    }
    public record CouponResponse(String code, BigDecimal discountAmount,
                                 LocalDateTime validFrom, LocalDateTime validTo, boolean valid) {
    }


}
