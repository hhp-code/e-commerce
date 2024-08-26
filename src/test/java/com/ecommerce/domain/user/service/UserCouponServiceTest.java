package com.ecommerce.domain.user.service;

import com.ecommerce.config.DatabaseCleanUp;
import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.User;
import com.ecommerce.interfaces.exception.domain.UserException;
import com.ecommerce.domain.coupon.DiscountType;
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
public class UserCouponServiceTest {
    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }


    @BeforeEach
    void setup(){
        User testUser = new User( "testUser", BigDecimal.ZERO);
        User user = userService.saveUser(testUser);
        List<CouponWrite> testCoupons = Arrays.asList(
                new CouponWrite("testCoupon1", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 10),
                new CouponWrite("testCoupon2", BigDecimal.valueOf(2000), DiscountType.FIXED_AMOUNT, 10));
        for(CouponWrite coupon : testCoupons){
            user.addCoupon(coupon);
        }
        userService.saveUser(user);
    }


    @Test
    @DisplayName("사용자의 쿠폰 목록 조회 실패 - 사용자에게 발급된 쿠폰이 없는 경우")
    void getUserCouponsFail() {
        //given
        long userId = 2L;
        long couponId = 1L;

        //when && then
        assertThrows(UserException.ServiceException.class, () -> userCouponService.getUserCoupon(userId, couponId));
    }


}
