package com.ecommerce.api.balance;

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
    public BalanceResponse getBalance(@PathVariable Long userId) {
        BigDecimal balance = userBalances.getOrDefault(userId, BigDecimal.ZERO);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("balance", balance);
        data.put("lastUpdated", LocalDateTime.now());
        return new BalanceResponse(true, "잔액 조회 성공", data);
    }
    @PostMapping("/users/{id}/balance")
    public BalanceResponse chargeBalance(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        BigDecimal currentBalance = userBalances.getOrDefault(id, BigDecimal.ZERO);
        BigDecimal newBalance = currentBalance.add(amount);
        userBalances.put(id, newBalance);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("balance", newBalance);
        return new BalanceResponse(true, "충전 성공", new HashMap<>(response));
    }
    record BalanceResponse(boolean success, String message, Map<String, Object> data) {}
}
