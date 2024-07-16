package com.ecommerce.api.controller.domain.user;

import com.ecommerce.api.controller.domain.coupon.dto.CouponDto;
import com.ecommerce.api.controller.domain.coupon.dto.CouponMapper;
import com.ecommerce.api.controller.domain.user.dto.UserDto;
import com.ecommerce.api.controller.domain.user.dto.UserMapper;
import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user_coupon", description = "사용자 Coupon API")
@RestController
@RequestMapping("/api")
public class UserCouponController {
    private final CouponUseCase couponUseCase;
    private final CouponQueueManager couponQueueManager;
    private final UserService userService;

    public UserCouponController(CouponUseCase couponUseCase, CouponQueueManager couponQueueManager, UserService userService) {
        this.couponUseCase = couponUseCase;
        this.couponQueueManager = couponQueueManager;
        this.userService = userService;
    }
    @PostMapping("/users/{userId}/coupons")
    @Operation(summary = "사용자에게 쿠폰 발급", description = "특정 사용자에게 쿠폰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공",
                            content = @Content(schema = @Schema(implementation = UserDto.UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            })
    public UserDto.UserResponse issueCouponToUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @RequestBody Long couponId) {
        return UserMapper.toUserAsyncResponse(
                couponQueueManager.addToQueueAsync(CouponMapper.toUserCouponCommand(userId, couponId))
        );
    }
    @GetMapping("/users/{userId}/coupon/status")
    @Operation(summary = "쿠폰 발급 요청 상태 확인", description = "쿠폰 발급 요청의 처리 상태를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 상태 확인 성공",
                            content = @Content(schema = @Schema(implementation = UserDto.IssueStatusResponse.class))),
                    @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음")
            })
    public UserDto.IssueStatusResponse checkCouponIssueStatus(
            @Parameter(description = "사용자 ID") @PathVariable Long userId){
        return UserMapper.toIssueStatusResponse(couponQueueManager.checkStatus(userId));
    }
    @GetMapping("/users/{userId}/coupons")
    @Operation(summary = "사용자의 쿠폰 목록 조회", description = "특정 사용자의 모든 쿠폰을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = UserDto.UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            })
    public List<UserDto.CouponResponse> getUserCoupons(@Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return UserMapper.toUserCouponResponseList(userService.getUserCoupons(userId));
    }


    @PostMapping("/users/{userId}/coupons/{userCouponId}/use")
    @Operation(summary = "쿠폰 사용", description = "사용자의 특정 쿠폰을 사용합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자 또는 쿠폰을 찾을 수 없음")
            })
    public UserDto.UserResponse useCoupon(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "사용자 쿠폰 ID") @PathVariable Long userCouponId) {
        return UserMapper.toUserResponse(couponUseCase.useCoupon(userId, userCouponId));
    }


}
