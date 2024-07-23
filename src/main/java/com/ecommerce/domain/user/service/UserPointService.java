package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
public class UserPointService {
    private final UserRepository userRepository;

    public UserPointService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal getPoint(Long userId) {
        return userRepository.getAmountByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public BigDecimal chargePoint(long userId, BigDecimal amount) {
        User user = userRepository.saveChargeAmount(userId, amount).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getPoint();
    }

    @Transactional
    public BigDecimal deductPoint(long userId, BigDecimal totalAmount) {
        User user = userRepository.saveDeductAmount(userId, totalAmount).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getPoint();

    }

}
