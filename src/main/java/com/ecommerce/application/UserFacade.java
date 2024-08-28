package com.ecommerce.application;

import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class UserFacade {

    private final QuantumLockManager quantumLockManager;
    private final UserService userService;


    public UserFacade(QuantumLockManager quantumLockManager, UserService userService) {
        this.quantumLockManager = quantumLockManager;
        this.userService = userService;
    }


    public User chargePoint(Long userId, BigDecimal amount) {
        String lockKey = "user:" + userId;
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout,
                    () -> {
                        User user = userService.getUser(userId)
                                .chargePoint(amount);
                        return userService.saveUser(user);
                    });
        }
        catch (TimeoutException e) {
            throw new UserException("포인트 충전 중 락 획득 시간 초과");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User deductPoint(Long userId, BigDecimal amount) {
        String lockKey = "user:" + userId;
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout,
                    () -> {
                        User user = userService.getUser(userId)
                                .deductPoint(amount);
                        return userService.saveUser(user);
                    });
        } catch (TimeoutException | InterruptedException e) {
            throw new UserException.ServiceException("포인트 감소 중 락 획득 시간 초과");
        }
    }

}
