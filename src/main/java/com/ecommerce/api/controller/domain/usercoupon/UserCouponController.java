package com.ecommerce.api.controller.domain.usercoupon;

import com.ecommerce.api.controller.domain.coupon.dto.CouponDto;
import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.api.controller.domain.usercoupon.dto.UserCouponDto;
import com.ecommerce.api.controller.domain.usercoupon.dto.UserCouponMapper;
import com.ecommerce.domain.usercoupon.service.UserCouponService;
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
    private final UserCouponService userCouponService;
    private final CouponUseCase couponUseCase;

    public UserCouponController(UserCouponService userCouponService, CouponUseCase couponUseCase) {
        this.userCouponService = userCouponService;
        this.couponUseCase = couponUseCase;
    }
    @PostMapping("/users/{userId}/coupons")
    @Operation(summary = "사용자에게 쿠폰 발급", description = "특정 사용자에게 쿠폰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공",
                            content = @Content(schema = @Schema(implementation = UserCouponDto.UserCouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            })
    public UserCouponDto.UserCouponResponse issueCouponToUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @RequestBody UserCouponDto.UserCouponRequest request) {
        return UserCouponMapper.toUserCouponResponse(
                userCouponService.issueCouponToUser(UserCouponMapper.toUserCouponCommand(userId, request))
        );
    }
    @GetMapping("/users/{userId}/coupons")
    @Operation(summary = "사용자의 쿠폰 목록 조회", description = "특정 사용자의 모든 쿠폰을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = UserCouponDto.UserCouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            })
    public List<UserCouponDto.UserCouponResponse> getUserCoupons(@Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return UserCouponMapper.toUserCouponResponseList(userCouponService.getUserCoupons(userId));
    }


    @PostMapping("/users/{userId}/coupons/{userCouponId}/use")
    @Operation(summary = "쿠폰 사용", description = "사용자의 특정 쿠폰을 사용합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자 또는 쿠폰을 찾을 수 없음")
            })
    public UserCouponDto.UserCouponResponse useCoupon(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "사용자 쿠폰 ID") @PathVariable Long userCouponId) {
        return UserCouponMapper.toUserCouponResponse(couponUseCase.useCoupon(userId, userCouponId));
    }


}
