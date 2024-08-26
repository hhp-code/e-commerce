package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.CouponDomainMapper;
import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.UserDomainMapper;
import com.ecommerce.domain.user.User;
import com.ecommerce.infra.coupon.entity.CouponEntity;
import com.ecommerce.infra.user.entity.UserEntity;
import com.ecommerce.interfaces.exception.domain.UserException;
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
    public CouponWrite getUserCoupon(long userId, long couponId) {
        CouponEntity coupon = userRepository.getCouponByUser(userId, couponId).orElseThrow(
                () -> new UserException.ServiceException("사용자에게 발급된 쿠폰을 찾을 수 없습니다.")
        );
        return CouponDomainMapper.toCouponWrite(coupon);
    }

    @Transactional
    public User updateUserCoupon(User user , CouponWrite userCoupon) {
        user.addCoupon(userCoupon);
        UserEntity entity = UserDomainMapper.toEntity(user);
        UserEntity userEntity = userRepository.save(entity).orElseThrow(
                () -> new UserException.ServiceException("사용자 쿠폰 정보를 업데이트할 수 없습니다.")
        );
        return UserDomainMapper.toWriteModel(userEntity);
    }
}
