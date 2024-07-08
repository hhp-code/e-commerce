package com.ecommerce.api.balance.controller.dto;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Map;

@UtilityClass
public class BalanceDto {
    public record BalanceResponse(boolean success, String message, Map<String, Object> data) {
    }
    public record BalanceRequest(BigDecimal amount) {
    }

}
