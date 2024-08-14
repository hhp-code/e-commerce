package com.ecommerce.domain.user.command;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class UserCommand {
    public record Charge(Long userId, BigDecimal amount) {
    }
}
