package com.ecommerce.application.usecase;

import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.service.OrderCommandService;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderQuery;
import com.ecommerce.domain.order.service.OrderQueryService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserCouponService;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Component
public class CouponUseCase {
    private final OrderCommandService orderCommandService;
    private final CouponService couponService;
    private final UserService userService;
    private final UserCouponService userCouponService;
    private final QuantumLockManager quantumLockManager;
    private final OrderQueryService orderQueryService;

    public CouponUseCase(OrderCommandService orderCommandService,
                         CouponService couponService, UserService userService, UserCouponService userCouponService, QuantumLockManager quantumLockManager, OrderQueryService orderQueryService) {
        this.orderCommandService = orderCommandService;
        this.couponService = couponService;
        this.userService = userService;
        this.userCouponService = userCouponService;
        this.quantumLockManager = quantumLockManager;
        this.orderQueryService = orderQueryService;
    }

    public User useCoupon(Long userId, Long couponId) {
        User user = userService.getUser(userId);
        Coupon userCoupon = userCouponService.getUserCoupon(userId, couponId);
        OrderQuery.GetOrder getOrderQuery = new OrderQuery.GetOrder(userId);
        OrderInfo.Detail order = orderQueryService.getOrder(getOrderQuery);
//        order.applyCoupon(userCoupon);
//        orderService.saveAndGet(order);
//        return userCouponService.updateUserCoupon(user,userCoupon);
        return null;
    }

    @Transactional
    public User issueCouponToUser(CouponCommand.Issue issue) {
        String lockKey = "coupon:" + issue.couponId();
        Duration timeout = Duration.ofSeconds(5);
        try{
            return quantumLockManager.executeWithLock(lockKey, timeout, () ->
            {
                User user = userService.getUser(issue.userId());
                Coupon coupon = couponService.deductCoupon(issue.couponId());
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