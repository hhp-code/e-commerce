package com.ecommerce.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public UserResponse getUserInfo(@PathVariable Long id) {
        return new UserResponse(id, "testUser", BigDecimal.valueOf(10000),  false, LocalDateTime.now());
    }

    @PostMapping("/{id}/balance")
    public BalanceResponse chargeBalance(@PathVariable Long id, @RequestBody BalanceRequest request) {
        BigDecimal newBalance = BigDecimal.valueOf(10000).add(request.amount());
        return new BalanceResponse(id, newBalance);
    }

    @GetMapping("/balance/{userId}")
    public BalanceResponse getBalance(@PathVariable Long userId) {
        return new BalanceResponse(userId, BigDecimal.valueOf(10000) );
    }

    record UserResponse(Long id, String username, BigDecimal balance, Boolean isDeleted, LocalDateTime createdAt) { }
    record BalanceRequest(BigDecimal amount) { }
    record BalanceResponse(Long userId, BigDecimal balance) { }
}