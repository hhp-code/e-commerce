package com.ecommerce.api.user.controller.dto;

import java.math.BigDecimal;

public record BalanceResponse(Long userId, BigDecimal balance) {
}
