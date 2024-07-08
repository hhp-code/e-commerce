package com.ecommerce.api.balance.controller.dto;

import java.math.BigDecimal;
import java.util.Map;

public class BalanceMapper {
    public static BalanceDto.BalanceResponse toResponse(BigDecimal result) {
        if(result !=null){
            String message = "Success";
            return new BalanceDto.BalanceResponse(true, message, Map.of("balance", result));
        }
        return new BalanceDto.BalanceResponse(false,"balance is not found", Map.of());
    }

}
