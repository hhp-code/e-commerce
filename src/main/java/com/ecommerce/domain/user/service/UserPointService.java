package com.ecommerce.domain.user.service;

import com.ecommerce.api.exception.domain.UserException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
@Slf4j
@Component
public class UserPointService {
    private final UserRepository userRepository;

    private final QuantumLockManager quantumLockManager;


    public UserPointService(UserRepository userRepository, QuantumLockManager quantumLockManager) {
        this.userRepository = userRepository;
        this.quantumLockManager = quantumLockManager;
    }

    @Transactional(readOnly = true)
    public BigDecimal getPoint(Long userId) {
        return userRepository.getAmountByUserId(userId).orElseThrow(() ->
                new UserException.ServiceException("사용자를 찾을수 없습니다."));
    }


    public BigDecimal chargePoint(Long userId, BigDecimal amount) {
        String lockKey = "user:" + userId;
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                log.info("chargePoint lockKey: {}", lockKey);
                User user = userRepository.getById(userId)
                        .orElseThrow(() -> new UserException.ServiceException("사용자를 찾을 수 없습니다."));
                BigDecimal newBalance = user.chargePoint(amount);
                userRepository.save(user);
                return newBalance;
            });
        }
        catch (TimeoutException e) {
            throw new UserException.ServiceException("포인트 충전 중 락 획득 시간 초과");
        } catch (Exception e) {
            throw new UserException.ServiceException("포인트 충전 중 오류 발생");
        }
    }

    public BigDecimal deductPoint(Long userId, BigDecimal amount) {
        String lockKey = "user:" + userId;
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                User user = userRepository.getById(userId)
                        .orElseThrow(() -> new UserException.ServiceException("사용자를 찾을 수 없습니다."));
                BigDecimal newBalance = user.deductPoint(amount);
                userRepository.save(user);
                return newBalance;
            });
        } catch (TimeoutException e) {
            throw new UserException.ServiceException("포인트 감소 중 락 획득 시간 초과");
        } catch (Exception e) {
            throw new UserException.ServiceException("포인트 감소 중 오류 발생");
        }
    }
}
