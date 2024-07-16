package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CouponUseCase {
    private final OrderService orderService;
    private final CouponService couponService;
    private final UserService userService;

    public CouponUseCase(OrderService orderService,
                         CouponService couponService, UserService userService) {
        this.orderService = orderService;
        this.couponService = couponService;
        this.userService = userService;
    }

    public User useCoupon(Long userId, Long couponId) {
        User user = userService.getUser(userId);
        Coupon coupon = couponService.getCoupon(couponId);
        Order order = orderService.getOrderByUserId(userId);
        order.applyCoupon(coupon);
        coupon.use();
        couponService.updateCoupon(coupon);
        orderService.saveAndGet(order);
        return userService.updateUserCoupon(coupon);

    }

    public User issueCouponToUser(CouponCommand.Issue issue) {
        User user = userService.getUser(issue.userId());
        Coupon coupon = couponService.decrementCouponQuantity(issue.couponId());
        user.addCoupon(coupon);
        return userService.updateUserCoupon(coupon);
    }

}