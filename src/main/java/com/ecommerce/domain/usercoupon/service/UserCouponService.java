package com.ecommerce.domain.usercoupon.service;

import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.service.repository.UserCouponRepository;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.usercoupon.UserCoupon;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;
    private final UserService userService;
    private final CouponService couponService;

    public UserCouponService(UserCouponRepository userCouponRepository, UserService userService, CouponService couponService) {
        this.userCouponRepository = userCouponRepository;
        this.userService = userService;
        this.couponService = couponService;
    }

    @Transactional
    public UserCoupon issueCouponToUser(UserCouponCommand.UserCouponCreate create) {
        User user = userService.getUser(create.userId());
        Coupon coupon = couponService.getCoupon(create.request().couponId());
        user.addCoupon(coupon);
        return userCouponRepository.getCouponByUser(user, coupon).orElseThrow(
                () -> new RuntimeException("이미 사용자에게 발급된 쿠폰입니다.")
        );
    }

    @Transactional(readOnly = true)
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.getAllCouponsByUserId(userId);
    }
    @Transactional(readOnly =true)
    public UserCoupon getUserCoupon(User user, Coupon coupon) {
        return userCouponRepository.getCouponByUser(user, coupon).orElseThrow(
                () -> new RuntimeException("사용자에게 발급된 쿠폰을 찾을 수 없습니다.")
        );
    }

    public UserCoupon updateUserCoupon(UserCoupon userCoupon) {
        return userCouponRepository.save(userCoupon).orElseThrow(
                () -> new RuntimeException("사용자 쿠폰을 업데이트할 수 없습니다."));
    }
}