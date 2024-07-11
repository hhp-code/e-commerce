package com.ecommerce.api.controller.domain.user.dto;

import com.ecommerce.domain.user.service.UserBalanceCommand;

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
    public static UserBalanceCommand.Create toCommand(Long userId, UserDto.UserBalanceRequest request) {
        return new UserBalanceCommand.Create(userId, request.amount());
    }

}
