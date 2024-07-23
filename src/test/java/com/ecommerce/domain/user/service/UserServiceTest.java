package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    private List<Coupon> testCoupons;

    @BeforeEach
    void setup(){
        User testUser = new User(1L, "testUser", BigDecimal.ZERO);
        User user = userService.saveUser(testUser);
        testCoupons = Arrays.asList(
                new Coupon("testCoupon1", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT,10),
                new Coupon("testCoupon2", BigDecimal.valueOf(2000), DiscountType.FIXED_AMOUNT,10));
        for(Coupon coupon : testCoupons){
            user.addCoupon(coupon);
        }
        userService.saveUser(user);
    }

    @Test
    @DisplayName("사용자의 쿠폰 목록 조회 성공")
    void getUserCouponsSuccess() {
        // Given
        Long userId = 1L;

        // When
        List<Coupon> result = userService.getUserCoupons(userId);

        // Then
        assertNotNull(result);
        assertEquals(testCoupons.size(), result.size());
    }


}