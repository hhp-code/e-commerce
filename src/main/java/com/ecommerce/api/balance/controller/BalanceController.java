package com.ecommerce.api.balance.controller;

import com.ecommerce.api.balance.BalanceService;
import com.ecommerce.api.balance.controller.dto.BalanceDto;
import com.ecommerce.api.balance.controller.dto.BalanceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "balance", description = "잔액 관련 API")
@RestController
@RequestMapping("/api")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balance/{userId}")
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    public BalanceDto.BalanceResponse getBalance(@PathVariable Long userId) {
        return BalanceMapper.toResponse(balanceService.getBalance(userId));
    }
    @PostMapping("/balance/{userId}/charge")
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    public BalanceDto.BalanceResponse chargeBalance(@PathVariable Long userId, @RequestBody BalanceDto.BalanceRequest request) {

        return BalanceMapper.toResponse(balanceService.chargeBalance(userId, request));
    }
}

