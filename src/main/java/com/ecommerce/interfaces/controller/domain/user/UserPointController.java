package com.ecommerce.interfaces.controller.domain.user;

import com.ecommerce.application.CommandHandler;
import com.ecommerce.interfaces.controller.domain.user.dto.UserDto;
import com.ecommerce.interfaces.controller.domain.user.dto.UserBalanceMapper;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.interfaces.controller.domain.user.dto.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
@Slf4j
@Tag(name = "user_balance", description = "잔액 관련 API")
@RestController
@RequestMapping("/api")
public class UserPointController {

    private final UserService userService;
    private final CommandHandler commandHandler;

    public UserPointController(UserService userService, CommandHandler commandHandler) {
        this.userService = userService;
        this.commandHandler = commandHandler;
    }

    @GetMapping("/point/{userId}")
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserBalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public UserDto.UserBalanceResponse getBalance(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return UserBalanceMapper.toResponse(
                userService.getUser(userId));
    }

    @PostMapping("/point/{userId}/charge")
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 충전 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserBalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<String> chargeBalance(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "충전 요청 정보") @RequestBody BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("충전 금액은 0보다 커야 합니다.");
        }
        commandHandler.handle(UserMapper.toCharge(userId, amount));
        return ResponseEntity.ok("잔액이 충전되었습니다.");
    }
}