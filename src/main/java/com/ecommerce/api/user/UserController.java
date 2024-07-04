package com.ecommerce.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable Long id) {
        UserResponse user = new UserResponse(id, "testUser", BigDecimal.valueOf(10000), 1, false, LocalDateTime.now());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> chargeBalance(@PathVariable Long id, @RequestBody BalanceRequest request) {
        BigDecimal newBalance = BigDecimal.valueOf(10000).add(request.amount());
        BalanceResponse response = new BalanceResponse(id, newBalance, 2);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long userId) {
        BalanceResponse response = new BalanceResponse(userId, BigDecimal.valueOf(10000),2 );
        return ResponseEntity.ok(response);
    }

    record UserResponse(Long id, String username, BigDecimal balance, Integer version, Boolean isDeleted, LocalDateTime createdAt) { }
    record BalanceRequest(BigDecimal amount) { }
    record BalanceResponse(Long userId, BigDecimal balance, Integer version) { }
}