package com.ecommerce.api.balance.controller.dto;

import com.ecommerce.api.balance.service.BalanceCommand;

import java.math.BigDecimal;
import java.util.Map;

public class BalanceDtoMapper {
    public static BalanceDto.BalanceResponse toResponse(BigDecimal result) {
        if(result !=null){
            String message = "Success";
            return new BalanceDto.BalanceResponse(true, message, Map.of("balance", result));
        }
        return new BalanceDto.BalanceResponse(false,"balance is not found", Map.of());
    }
    public static BalanceCommand.Create toCommand(Long userId, BalanceDto.BalanceRequest request) {
        return new BalanceCommand.Create(userId, request.amount());
    }

}
