package com.ecommerce.application.usecase;

import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserCouponService;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Component
public class CouponUseCase {
    private final CouponService couponService;
    private final UserService userService;
    private final UserCouponService userCouponService;
    private final QuantumLockManager quantumLockManager;
    private final OrderService orderService;

    public CouponUseCase(CouponService couponService, UserService userService, UserCouponService userCouponService, QuantumLockManager quantumLockManager, OrderService orderService) {
        this.couponService = couponService;
        this.userService = userService;
        this.userCouponService = userCouponService;
        this.quantumLockManager = quantumLockManager;
        this.orderService = orderService;
    }

    public User useCoupon(Long userId, Long couponId) {
        User user = userService.getUser(userId);
        CouponWrite userCoupon = userCouponService.getUserCoupon(userId, couponId);
        OrderQuery.GetOrder getOrderQuery = new OrderQuery.GetOrder(userId);
        OrderWrite orderEntity = orderService.getOrder(getOrderQuery.orderId());

        orderEntity.applyCoupon(userCoupon);
        orderService.saveOrder(orderEntity);
        return userCouponService.updateUserCoupon(user,userCoupon);
    }

    @Transactional
    public User issueCouponToUser(CouponCommand.Issue issue) {
        String lockKey = "coupon:" + issue.couponId();
        Duration timeout = Duration.ofSeconds(5);
        try{
            return quantumLockManager.executeWithLock(lockKey, timeout, () ->
            {
                User user = userService.getUser(issue.userId());
                CouponWrite coupon = couponService.deductCoupon(issue.couponId());
                user.addCoupon(coupon);
                user.getCoupons().size();
                return userCouponService.updateUserCoupon(user, coupon);
            });
        }
        catch (Exception e){
            throw new RuntimeException("Coupon issue failed", e);
        }
    }

}