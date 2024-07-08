package com.ecommerce.api.balance.controller;

import java.math.BigDecimal;

record BalanceRequest(BigDecimal amount) {
}
