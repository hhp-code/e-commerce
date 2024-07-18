package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.user.service.UserCouponService;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CouponUseCase {
    private final OrderService orderService;
    private final CouponService couponService;
    private final UserService userService;
    private final UserCouponService userCouponService;

    public CouponUseCase(OrderService orderService,
                         CouponService couponService, UserService userService, UserCouponService userCouponService) {
        this.orderService = orderService;
        this.couponService = couponService;
        this.userService = userService;
        this.userCouponService = userCouponService;
    }

    public User useCoupon(Long userId, Long couponId) {
        User user = userService.getUser(userId);
        Coupon userCoupon = userCouponService.getUserCoupon(userId, couponId);
        Order order = orderService.getOrderByUserId(userId);
        order.applyCoupon(userCoupon);
        orderService.saveAndGet(order);
        return userCouponService.updateUserCoupon(user,userCoupon);
    }

    @Transactional
    public User issueCouponToUser(CouponCommand.Issue issue) {
        User user = userService.getUser(issue.userId());
        Coupon coupon = couponService.deductCoupon(issue.couponId());
        user.addCoupon(coupon);
        return userCouponService.updateUserCoupon(user, coupon);
    }

}