package com.ecommerce.interfaces.controller.domain.user.dto;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Map;

@UtilityClass
public class UserDto {
    public record UserBalanceResponse(boolean success, String message, Map<String, Object> data) {
    }
    public record UserResponse(String username, BigDecimal balance) {
    }


}
