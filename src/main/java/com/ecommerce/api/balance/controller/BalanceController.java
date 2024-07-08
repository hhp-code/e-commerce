package com.ecommerce.api.balance.controller;

import com.ecommerce.api.balance.BalanceRequest;
import com.ecommerce.api.balance.BalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Tag(name = "balance", description = "잔액 관련 API")
@RestController
@RequestMapping("/api")
public class BalanceController {

    private final Map<Long, BigDecimal> userBalances = new HashMap<>();
    @GetMapping("/balance/{userId}")
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    public BalanceResponse getBalance(@PathVariable Long userId) {
        BigDecimal balance = userBalances.getOrDefault(userId, BigDecimal.ZERO);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("balance", balance);
        data.put("lastUpdated", LocalDateTime.now());
        return new BalanceResponse(true, "잔액 조회 성공", data);
    }
    @PostMapping("/users/{id}/balance")
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    public BalanceResponse chargeBalance(@PathVariable Long id, @RequestBody BalanceRequest request) {
        BigDecimal amount = request.amount();
        BigDecimal currentBalance = userBalances.getOrDefault(id, BigDecimal.ZERO);
        BigDecimal newBalance = currentBalance.add(amount);
        userBalances.put(id, newBalance);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("balance", newBalance);
        return new BalanceResponse(true, "충전 성공", new HashMap<>(response));
    }
}

