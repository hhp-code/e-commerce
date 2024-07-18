package com.ecommerce.api.controller.domain.user;

import com.ecommerce.api.controller.domain.user.dto.UserDto;
import com.ecommerce.api.controller.domain.user.dto.UserMapper;
import com.ecommerce.domain.user.service.UserPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "user_balance", description = "잔액 관련 API")
@RestController
@RequestMapping("/api")
public class UserPointController {

    private final UserPointService userPointService;

    public UserPointController(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    @GetMapping("/balance/{userId}")
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserBalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public UserDto.UserBalanceResponse getBalance(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return UserMapper.toBalanceResponse(
                userPointService.getPoint(userId));
    }

    @PostMapping("/balance/{userId}/charge")
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 충전 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserBalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public UserDto.UserBalanceResponse chargeBalance(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "충전 요청 정보") @RequestBody BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        return UserMapper.toBalanceResponse(
                userPointService.chargePoint(userId, amount));
    }
}