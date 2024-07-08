package com.ecommerce.api.user.controller;

import java.math.BigDecimal;

record BalanceResponse(Long userId, BigDecimal balance) {
}
