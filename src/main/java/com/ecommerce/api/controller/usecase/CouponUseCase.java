package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.usercoupon.service.UserCouponService;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.usercoupon.UserCoupon;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.user.service.UserService;

public class CouponUseCase {
    private final UserCouponService userCouponService;
    private final UserService userService;
    private final OrderService orderService;
    private final CouponService couponService;

    public CouponUseCase(UserService userService, OrderService orderService, UserCouponService userCouponRepository, CouponService couponRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.userCouponService = userCouponRepository;
        this.couponService = couponRepository;
    }

    public UserCoupon useCoupon(Long userId, Long couponId) {
        User user = userService.getUser(userId);
        Order order = orderService.getOrder(userId);
        Coupon coupon = couponService.getCoupon(couponId);
        UserCoupon userCoupon = userCouponService.getUserCoupon(user,coupon);

        if (userCoupon.isUsed()) {
            throw new RuntimeException("이미 사용된 쿠폰입니다.");
        }

        if (!coupon.isValid()) {
            throw new RuntimeException("유효하지 않은 쿠폰입니다.");
        }
        coupon.setQuantity(coupon.getQuantity()-1);
        order.applyCoupon(coupon);
        userCoupon.use();

        return userCouponService.updateUserCoupon(userCoupon);
    }
}