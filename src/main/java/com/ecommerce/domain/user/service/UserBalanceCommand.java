package com.ecommerce.domain.user.service;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class UserBalanceCommand {
    public record Create(long userId, BigDecimal amount) { }
}
