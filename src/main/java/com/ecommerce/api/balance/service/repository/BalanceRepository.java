package com.ecommerce.api.balance.service.repository;

import com.ecommerce.domain.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface BalanceRepository {
    Optional<BigDecimal> getAmountByUserId(Long userId);

    Optional<User> saveChargeAmount(User user);

    Optional<User> getUserByRequest(Long userId);
}
