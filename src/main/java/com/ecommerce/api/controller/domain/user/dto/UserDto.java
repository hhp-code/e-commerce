package com.ecommerce.api.controller.domain.user.dto;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Map;

@UtilityClass
public class UserDto {
    public record UserBalanceResponse(boolean success, String message, Map<String, Object> data) {
    }
    public record UserBalanceRequest(BigDecimal amount) {
        public void validate() {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("금액은 0하고 같거나 커야 합니다.");
            }
        }
    }

}
