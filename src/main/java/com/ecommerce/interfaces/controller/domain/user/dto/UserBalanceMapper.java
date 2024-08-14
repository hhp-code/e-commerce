package com.ecommerce.interfaces.controller.domain.user.dto;

import com.ecommerce.domain.user.UserWrite;

import java.math.BigDecimal;
import java.util.Map;

public class UserBalanceMapper {
    public static UserDto.UserBalanceResponse toResponse(UserWrite user) {
        BigDecimal result = user.getPoint();
        if(result !=null){
            String message = "Success";
            return new UserDto.UserBalanceResponse(true, message, Map.of("balance", result));
        }
        return new UserDto.UserBalanceResponse(false,"balance is not found", Map.of());
    }

}
