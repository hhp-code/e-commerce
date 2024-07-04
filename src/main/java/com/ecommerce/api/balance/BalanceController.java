package com.ecommerce.api.balance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BalanceController {

    private final Map<Long, BigDecimal> userBalances = new HashMap<>();

    @GetMapping("/balance/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        BigDecimal balance = userBalances.getOrDefault(userId, BigDecimal.ZERO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "잔액 조회 성공");
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("balance", balance);
        data.put("lastUpdated", LocalDateTime.now());
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/balance")
    public ResponseEntity<?> chargeBalance(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        if (amount.compareTo(new BigDecimal("1000")) < 0 || amount.compareTo(new BigDecimal("1000000")) > 0) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "충전 금액은 1,000원 이상 1,000,000원 이하여야 합니다.");
            errorResponse.put("errorCode", "INVALID_CHARGE_AMOUNT");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        BigDecimal currentBalance = userBalances.getOrDefault(id, BigDecimal.ZERO);
        BigDecimal newBalance = currentBalance.add(amount);
        userBalances.put(id, newBalance);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("balance", newBalance);
        response.put("version", 1);
        return ResponseEntity.ok(response);
    }
}