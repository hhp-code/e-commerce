package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.UserWrite;
import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @Test
    @DisplayName("사용자의 쿠폰 목록 조회 실패 - 사용자에게 발급된 쿠폰이 없는 경우")
    void getUserCouponsFail() {
        // Given
        long userId = 1L;
        long couponId = 1L;
        when(userRepository.getCouponByUser(userId,couponId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserException.ServiceException.class, () -> userCouponService.getUserCoupon(userId, couponId));
    }
    @Test
    @DisplayName("사용자의 쿠폰 업데이트 실패 - 사용자 조회실패")
    void getUpdateCouponsFail(){
        // Given
        UserWrite user = new UserWrite( "testUser", BigDecimal.ZERO);
        CouponWrite coupon = new CouponWrite();

        // When & Then
        assertThrows(UserException.ServiceException.class, () -> userCouponService.updateUserCoupon(user, coupon));
    }
    @Test
    @DisplayName("사용자의 쿠폰 업데이트 실패 - 업데이트 실패")
    void getUpdateCouponsFail2(){
        // Given
        UserWrite user = new UserWrite( "testUser", BigDecimal.ZERO);
        CouponWrite coupon = new CouponWrite();

        // When & Then
        assertThrows(UserException.ServiceException.class, () -> userCouponService.updateUserCoupon(user, coupon));
    }

}
