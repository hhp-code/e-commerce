package com.ecommerce.api.controller.domain.user;

import com.ecommerce.api.controller.domain.user.dto.UserDto;
import com.ecommerce.api.controller.domain.user.dto.UserBalanceMapper;
import com.ecommerce.domain.user.service.UserService;
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
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
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
        return UserBalanceMapper.toResponse(
                userService.getBalance(userId));
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
            @Parameter(description = "충전 요청 정보") @RequestBody UserDto.UserBalanceRequest request) {
        request.validate();
        return UserBalanceMapper.toResponse(
                userService.chargeBalance(UserBalanceMapper.toCommand(userId, request)));
    }
}