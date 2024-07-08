package com.ecommerce.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Tag(name = "user", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회", description = "사용자 정보를 조회합니다.")
    public UserResponse getUserInfo(@PathVariable Long id) {
        return new UserResponse(id, "testUser", BigDecimal.valueOf(10000),  false, LocalDateTime.now());
    }

    @PostMapping("/{id}/balance")
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    public BalanceResponse chargeBalance(@PathVariable Long id, @RequestBody BalanceRequest request) {
        BigDecimal newBalance = BigDecimal.valueOf(10000).add(request.amount());
        return new BalanceResponse(id, newBalance);
    }

    @GetMapping("/balance/{userId}")
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    public BalanceResponse getBalance(@PathVariable Long userId) {
        return new BalanceResponse(userId, BigDecimal.valueOf(10000) );
    }

}

