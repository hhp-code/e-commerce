package com.ecommerce.api.controller.domain.user.dto;

import java.math.BigDecimal;
import java.util.Map;

public class UserBalanceMapper {
    public static UserDto.UserBalanceResponse toResponse(BigDecimal result) {
        if(result !=null){
            String message = "Success";
            return new UserDto.UserBalanceResponse(true, message, Map.of("balance", result));
        }
        return new UserDto.UserBalanceResponse(false,"balance is not found", Map.of());
    }

}
