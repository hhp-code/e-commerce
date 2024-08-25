package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.ecommerce.infra.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private List<CouponWrite> testCoupons;

    @BeforeEach
    void setUp() {
        testCoupons = Arrays.asList(new CouponWrite(), new CouponWrite());
    }

    @Test
    @DisplayName("사용자의 쿠폰 목록 조회 성공")
    void getUserCouponsSuccess() {
        // Given
        Long userId = 1L;
        when(userRepository.getById(userId)).thenReturn(
                Optional.of(new UserEntity(userId, "testUser", BigDecimal.ZERO, testCoupons))
        );

        // When
        List<CouponWrite> result = userService.getUserCoupons(userId);

        // Then
        assertNotNull(result);
        assertEquals(testCoupons.size(), result.size());
        verify(userRepository).getById(userId);
    }
}
