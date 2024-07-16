package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class UserBalanceService {
    private final UserBalanceRepository userBalanceRepository;

    public UserBalanceService(UserBalanceRepository userBalanceRepository) {
        this.userBalanceRepository = userBalanceRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        return userBalanceRepository.getAmountByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public BigDecimal chargeBalance(UserBalanceCommand.Create request) {
        return userBalanceRepository.getAmountByUserIdWithLock(request.userId())
                .map(currentBalance -> {
                    BigDecimal amountToAdd = Optional.ofNullable(request.amount()).orElse(BigDecimal.ZERO);
                    BigDecimal newBalance = currentBalance.add(amountToAdd);
                    userBalanceRepository.saveChargeAmount(request.userId(), newBalance);
                    return newBalance;
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void decreaseBalance(User user, BigDecimal totalAmount) {
        userBalanceRepository.getAmountByUserIdWithLock(user.getId())
                .map(currentBalance -> {
                    BigDecimal amountToSubtract = Optional.ofNullable(totalAmount).orElse(BigDecimal.ZERO);
                    BigDecimal newBalance = currentBalance.subtract(amountToSubtract);
                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("잔액이 부족합니다.");
                    }
                    userBalanceRepository.saveChargeAmount(user.getId(), newBalance);
                    return newBalance;
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

}
