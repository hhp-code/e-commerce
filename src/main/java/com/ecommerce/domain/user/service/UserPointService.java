package com.ecommerce.domain.user.service;

import com.ecommerce.api.exception.domain.UserException;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class UserPointService {
    private final UserRepository userRepository;

    public UserPointService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal getPoint(Long userId) {
        return userRepository.getAmountByUserId(userId).orElseThrow(() ->
                new UserException.ServiceException("사용자를 찾을수 없습니다."));
    }

    @Transactional
    public BigDecimal chargePoint(Long userId, BigDecimal amount) {
        User user = userRepository.getByIdWithLock(userId)
                .orElseThrow(() ->
                        new UserException.ServiceException("사용자를 찾을수 없습니다."));
        BigDecimal newBalance = user.chargePoint(amount);
        userRepository.save(user);
        return newBalance;
    }

    @Transactional
    public BigDecimal deductPoint(Long userId, BigDecimal amount) {
        User user = userRepository.getByIdWithLock(userId)
                .orElseThrow(() -> new UserException.ServiceException("사용자를 찾을수 없습니다."));
        BigDecimal newBalance = user.deductPoint(amount);
        userRepository.save(user);
        return newBalance;
    }

}
