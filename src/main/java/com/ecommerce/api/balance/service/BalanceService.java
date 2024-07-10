package com.ecommerce.api.balance.service;

import com.ecommerce.api.balance.service.repository.BalanceRepository;
import com.ecommerce.api.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        return balanceRepository.getAmountByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public BigDecimal chargeBalance(BalanceCommand.Create request) {
        User user = balanceRepository.getUserByRequest(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal newBalance = user.getBalance();
        newBalance = newBalance.add(request.amount());
        user.setBalance(newBalance);
        return newBalance;
    }
}
