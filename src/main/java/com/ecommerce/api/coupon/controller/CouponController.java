package com.ecommerce.api.coupon.controller;

import com.ecommerce.api.coupon.controller.dto.*;
import com.ecommerce.api.coupon.service.CouponCommand;
import com.ecommerce.api.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "coupon", description = "쿠폰 관련 API")
@RestController
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/coupons")
    @Operation(summary = "쿠폰 생성", description = "새로운 쿠폰을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 생성 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    public CouponDto.CouponResponse createCoupon(@RequestBody CouponDto.CouponRequest request) {
        return CouponMapper.toCouponResponse(
                couponService.createCoupon(CouponMapper.toCoupon(request))
        );
    }

    @PostMapping("/users/{userId}/coupons")
    @Operation(summary = "사용자에게 쿠폰 발급", description = "특정 사용자에게 쿠폰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.UserCouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            })
    public CouponDto.UserCouponResponse issueCouponToUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @RequestBody CouponDto.UserCouponRequest request) {
        return CouponMapper.toUserCouponResponse(
                couponService.issueCouponToUser(CouponMapper.toUserCouponCommand(userId, request))
        );
    }

    @PostMapping("/coupons/{couponId}/issue")
    @Operation(summary = "쿠폰 발급 요청", description = "특정 쿠폰의 발급을 요청합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 발급 요청 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
            })
    public CouponDto.CouponResponse requestCouponIssue(@Parameter(description = "쿠폰 ID") @PathVariable Long couponId) {
        return new CouponDto.CouponResponse(1L, "SUMMER2024", BigDecimal.valueOf(5000), 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    }

    @GetMapping("/users/{userId}/coupons")
    @Operation(summary = "사용자의 쿠폰 목록 조회", description = "특정 사용자의 모든 쿠폰을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.UserCouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            })
    public List<CouponDto.UserCouponResponse> getUserCoupons(@Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return CouponMapper.toUserCouponResponseList(couponService.getUserCoupons(userId));
    }

    @PostMapping("/users/{userId}/coupons/{userCouponId}/use")
    @Operation(summary = "쿠폰 사용", description = "사용자의 특정 쿠폰을 사용합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자 또는 쿠폰을 찾을 수 없음")
            })
    public CouponDto.UserCouponResponse useCoupon(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "사용자 쿠폰 ID") @PathVariable Long userCouponId) {
        return CouponMapper.toUserCouponResponse(couponService.useCoupon(userId, userCouponId));
    }


    @GetMapping("/coupons/{couponId}")
    @Operation(summary = "쿠폰 상세 정보 조회", description = "특정 쿠폰의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 상세 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
            })
    public CouponDto.CouponDetailResponse getCouponDetail(@Parameter(description = "쿠폰 ID") @PathVariable Long couponId) {
        return CouponMapper.toCouponDetailResponse(couponService.getCouponDetail(couponId));
    }
}