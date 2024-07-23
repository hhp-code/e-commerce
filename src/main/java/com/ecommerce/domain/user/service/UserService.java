package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public User getUser(Long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    @Transactional(readOnly = true)
    public List<Coupon> getUserCoupons(Long userId) {
        return userRepository.getAllCouponsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public User getUserByCoupon(Coupon userCoupon) {
        return userRepository.getUserByCoupon(userCoupon).orElseThrow(
                () -> new RuntimeException("사용자 정보를 찾을 수 없습니다.")
        );
    }

    @Transactional(readOnly =true)
    public Coupon getUserCoupon(long userId, long couponId) {
        return userRepository.getCouponByUser(userId, couponId).orElseThrow(
                () -> new RuntimeException("사용자에게 발급된 쿠폰을 찾을 수 없습니다.")
        );
    }

    @Transactional
    public User updateUserCoupon(User user , Coupon userCoupon) {
        User managedUser = userRepository.getById(user.getId())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        managedUser.addCoupon(userCoupon);
        return saveUser(user);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user).orElseThrow(
                () -> new RuntimeException("사용자 정보를 찾을 수 없습니다.")
        );
    }


    public void deleteAll() {
        userRepository.deleteAll();
    }


}
