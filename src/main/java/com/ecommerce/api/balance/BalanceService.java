package com.ecommerce.api.balance;

import com.ecommerce.api.balance.controller.dto.BalanceDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class BalanceService {
    public BigDecimal getBalance(Long userId) {
        //TODO:userid로 조회
        return BigDecimal.valueOf(100);
    }

    public BigDecimal chargeBalance(Long userId, BalanceDto.BalanceRequest request) {
        //TODO:userid로 조회
        BigDecimal amount = request.amount();
        return amount.add(amount);
    }
}
