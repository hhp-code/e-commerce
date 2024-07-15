package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        BigDecimal currentBalance = userBalanceRepository.getAmountByUserId(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal newBalance = currentBalance.add(request.amount());
        userBalanceRepository.saveChargeAmount(request.userId(), newBalance);
        return newBalance;
    }

    @Transactional
    public void decreaseBalance(User user, BigDecimal totalAmount) {
        BigDecimal currentBalance = userBalanceRepository.getAmountByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal newBalance = currentBalance.subtract(totalAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        userBalanceRepository.saveChargeAmount(user.getId(), newBalance);
    }

}
