package com.ecommerce.domain.user.service;

import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserCouponService {
    private final UserRepository userRepository;

    public UserCouponService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional(readOnly =true)
    public Coupon getUserCoupon(long userId, long couponId) {
        return userRepository.getCouponByUser(userId, couponId).orElseThrow(
                () -> new UserException.ServiceException("사용자에게 발급된 쿠폰을 찾을 수 없습니다.")
        );
    }

    @Transactional
    public User updateUserCoupon(User user , Coupon userCoupon) {
        user.addCoupon(userCoupon);
        return userRepository.save(user).orElseThrow(
                () -> new UserException.ServiceException("사용자 쿠폰 정보를 업데이트할 수 없습니다.")
        );
    }
}
