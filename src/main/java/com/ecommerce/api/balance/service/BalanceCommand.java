package com.ecommerce.api.balance.service;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class BalanceCommand {
    public record Create(long userId, BigDecimal amount) { }
}
