package com.ecommerce.domain.user.service.repository;

import com.ecommerce.domain.user.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserBalanceRepository {
    Optional<BigDecimal> getAmountByUserId(Long userId);

    Optional<User> saveChargeAmount(Long userId, BigDecimal amount);

    Optional<User> getUserByRequest(Long userId);

    Optional<BigDecimal> getAmountByUserIdWithLock(long userId);
}
