package com.ecommerce.application;

import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.config.RedisLockManager;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
public class UserFacade {

    private final RedisLockManager redisLockManager;
    private final UserService userService;

    private final Duration lockTimeout = Duration.ofSeconds(5);
    private final Duration actionTimeout = Duration.ofSeconds(5);

    public UserFacade(RedisLockManager redisLockManager, UserService userService) {
        this.redisLockManager = redisLockManager;
        this.userService = userService;
    }


    public User chargePoint(Long userId, BigDecimal amount) {
        String lockKey = "user:" + userId;
        try {
            return redisLockManager.executeWithLock(lockKey, lockTimeout,actionTimeout,
                    () -> {
                        User user = userService.getUser(userId)
                                .chargePoint(amount);
                        return userService.saveUser(user);
                    });
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public User deductPoint(Long userId, BigDecimal amount) {
        String lockKey = "user:" + userId;
        try {
            return redisLockManager.executeWithLock(lockKey, lockTimeout, actionTimeout,
                    () -> {
                        User user = userService.getUser(userId)
                                .deductPoint(amount);
                        return userService.saveUser(user);
                    });
        } catch (InterruptedException e) {
            throw new UserException.ServiceException("포인트 감소 중 락 획득 시간 초과");
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
