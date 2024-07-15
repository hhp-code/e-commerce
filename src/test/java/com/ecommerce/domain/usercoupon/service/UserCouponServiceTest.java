package com.ecommerce.domain.usercoupon.service;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private List<Coupon> testCoupons;

    @BeforeEach
    void setUp() {
        testCoupons = Arrays.asList(new Coupon(), new Coupon());
    }

    @Test
    @DisplayName("사용자의 쿠폰 목록 조회 성공")
    void getUserCouponsSuccess() {
        // Given
        Long userId = 1L;
        when(userRepository.getAllCouponsByUserId(userId)).thenReturn(testCoupons);

        // When
        List<Coupon> result = userService.getUserCoupons(userId);

        // Then
        assertNotNull(result);
        assertEquals(testCoupons.size(), result.size());
        verify(userRepository).getAllCouponsByUserId(userId);
    }
}
