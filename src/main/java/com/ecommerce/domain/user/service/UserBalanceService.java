package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
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
    public void decreaseBalance(Long id, BigDecimal totalAmount) {
        BigDecimal currentBalance = userBalanceRepository.getAmountByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal newBalance = currentBalance.subtract(totalAmount);
        userBalanceRepository.saveChargeAmount(id, newBalance);
    }
}
