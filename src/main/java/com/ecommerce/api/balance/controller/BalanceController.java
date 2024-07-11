package com.ecommerce.api.balance.controller;

import com.ecommerce.api.balance.service.BalanceService;
import com.ecommerce.api.balance.controller.dto.BalanceDto;
import com.ecommerce.api.balance.controller.dto.BalanceDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
                    content = @Content(schema = @Schema(implementation = BalanceDto.BalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public BalanceDto.BalanceResponse getBalance(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return BalanceDtoMapper.toResponse(balanceService.getBalance(userId));
    }

    @PostMapping("/balance/{userId}/charge")
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 충전 성공",
                    content = @Content(schema = @Schema(implementation = BalanceDto.BalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public BalanceDto.BalanceResponse chargeBalance(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "충전 요청 정보") @RequestBody BalanceDto.BalanceRequest request) {
        return BalanceDtoMapper.toResponse(
                balanceService.chargeBalance(BalanceDtoMapper.toCommand(userId, request)));
    }
}