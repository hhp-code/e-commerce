package com.ecommerce.api.balance.repository;

import com.ecommerce.api.balance.service.repository.BalanceRepository;
import com.ecommerce.api.domain.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
@Repository
public class BalanceRepositoryImpl implements BalanceRepository {
    private final BalanceJPARepository balanceJPARepository;

    public BalanceRepositoryImpl(BalanceJPARepository balanceJPARepository) {
        this.balanceJPARepository = balanceJPARepository;
    }


    @Override
    public Optional<BigDecimal> getAmountByUserId(Long userId) {
        BigDecimal amountById = balanceJPARepository.findAmountById(userId);
        return Optional.of(amountById);
    }

    @Override
    public Optional<User> saveChargeAmount(Long userId, BigDecimal amount) {
        int resultRow = balanceJPARepository.saveChargeAmount(amount, userId);
        if(resultRow == 0){
            return Optional.empty();
        }
        return balanceJPARepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByRequest(Long userId) {
       return balanceJPARepository.findById(userId);
    }
}
