package com.ecommerce.domain.user.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.user.UserWrite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class UserServiceTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private UserService userService;

    private List<CouponWrite> testCoupons;
    private UserWrite user;

    @BeforeEach
    void setup(){
        UserWrite testUser = new UserWrite( "testUser", BigDecimal.ZERO);
        UserWrite initialUser = userService.saveUser(testUser);
        testCoupons = Arrays.asList(
                new CouponWrite("testCoupon1", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT,10),
                new CouponWrite("testCoupon2", BigDecimal.valueOf(2000), DiscountType.FIXED_AMOUNT,10));
        for(CouponWrite coupon : testCoupons){
            initialUser.addCoupon(coupon);
        }
        user = userService.saveUser(initialUser);
    }

    @Test
    @DisplayName("사용자의 쿠폰 목록 조회 성공")
    void getUserCouponsSuccess() {
        // Given && When
        List<CouponWrite> result = userService.getUserCoupons(user.getId());

        // Then
        assertNotNull(result);
        assertEquals(testCoupons.size(), result.size());
    }


}