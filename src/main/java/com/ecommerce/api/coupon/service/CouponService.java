package com.ecommerce.api.coupon.service;

import com.ecommerce.api.coupon.service.repository.CouponRepository;
import com.ecommerce.api.coupon.service.repository.UserCouponRepository;
import com.ecommerce.api.order.service.repository.OrderRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.Order;
import com.ecommerce.api.domain.User;
import com.ecommerce.api.domain.UserCoupon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponService(CouponRepository couponRepository, OrderRepository orderRepository, UserRepository userRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Transactional
    public Coupon createCoupon(CouponCommand.CouponCreate create) {
        Coupon coupon = new Coupon(create.code(),
                create.discountAmount(),
                create.type(),
                create.remainingQuantity(),
                create.validFrom(),
                create.validTo(), true);
        return couponRepository.save(coupon).orElseThrow(
                () -> new RuntimeException("Coupon could not be created")
        );
    }

    @Transactional
    public UserCoupon issueCouponToUser(CouponCommand.UserCouponCreate create) {
        Long userId = create.userId();
        User user = userRepository.getById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Long couponId = create.request().couponId();
        Coupon coupon = couponRepository.getById(couponId).orElseThrow(
                () -> new RuntimeException("Coupon not found")
        );
        user.addCoupon(coupon);
        return userCouponRepository.getCouponByUser(user, coupon).orElseThrow(
                () -> new RuntimeException("Coupon already issued to user")
        );
    }

    @Transactional(readOnly = true)
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.getAllCouponsByUserId(userId);
    }

    @Transactional
    public UserCoupon useCoupon(Long userId, Long couponId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderRepository.getById(userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Coupon coupon = couponRepository.getById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        UserCoupon userCoupon = userCouponRepository.getCouponByUser(user, coupon)
                .orElseThrow(() -> new RuntimeException("User does not have this coupon"));

        if (userCoupon.isUsed()) {
            throw new RuntimeException("Coupon has already been used");
        }

        if (!coupon.isValid()) {
            throw new RuntimeException("Coupon is not valid");
        }
        coupon.setQuantity(coupon.getQuantity()-1);
        order.applyCoupon(coupon);
        userCoupon.use();

        return userCouponRepository.save(userCoupon).orElseThrow(
                () -> new RuntimeException("Coupon could not be used")
        );
    }

    public Coupon getCouponDetail(Long couponId) {
        return couponRepository.getById(couponId).orElseThrow(
                () -> new RuntimeException("Coupon not found")
        );
    }
}
