package com.ecommerce.api.balance.service;

import com.ecommerce.api.balance.service.repository.BalanceRepository;
import com.ecommerce.domain.User;
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

        BigDecimal newBalance = user.getBalance()
                .map(amount -> amount.add(request.amount()))
                .orElse(request.amount());

        user.setBalance(newBalance);
        balanceRepository.saveChargeAmount(user);  // 변경된 사용자 정보를 저장

        return newBalance;
    }
}
