package com.ecommerce.interfaces.controller.domain.coupon;

import com.ecommerce.interfaces.controller.domain.coupon.dto.CouponDto;
import com.ecommerce.interfaces.controller.domain.coupon.dto.CouponMapper;
import com.ecommerce.domain.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

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
        request.validate();
        return CouponMapper.toCouponResponse(
                couponService.createCoupon(CouponMapper.toCoupon(request))
        );
    }



    @GetMapping("/coupons/{couponId}")
    @Operation(summary = "쿠폰 상세 정보 조회", description = "특정 쿠폰의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 상세 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = CouponDto.CouponDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
            })
    public CouponDto.CouponDetailResponse getCouponDetail(@Parameter(description = "쿠폰 ID") @PathVariable Long couponId) {
        return CouponMapper.toCouponDetailResponse(couponService.getCoupon(couponId));
    }
}