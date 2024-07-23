package com.ecommerce.domain.coupon.service;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CouponServiceTest {

    @Autowired
    private CouponService couponService;



    @Test
    @DisplayName("쿠폰 생성 성공 시나리오")
    void createCoupon() {
        // given
        CouponCommand.Create create = new CouponCommand.Create(
                "testCoupon",
                BigDecimal.valueOf(1000), 10,
                DiscountType.FIXED_AMOUNT,  LocalDateTime.now(), LocalDateTime.now().plusDays(7),true);
        //when
        Coupon coupon = couponService.createCoupon(create);

        //then
        assertNotNull(coupon);
        assertEquals(create.code(), coupon.getCode());
        assertThat(create.discountAmount()).isEqualByComparingTo(coupon.getDiscountAmount());
        assertEquals(create.quantity(), coupon.getQuantity());
    }

    @Test
    @DisplayName("쿠폰 조회 성공 시나리오")
    void getCoupon() {
        //given
        User user = new User(1L, "testUser", BigDecimal.ZERO);
        Coupon coupon = new Coupon("testCoupon", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 10, LocalDateTime.now(), LocalDateTime.now().plusDays(7), true);
        user.addCoupon(coupon);
        couponService.createCoupon(new CouponCommand.Create("testCoupon", BigDecimal.valueOf(1000), 10, DiscountType.FIXED_AMOUNT, LocalDateTime.now(), LocalDateTime.now().plusDays(7), true));

        //when
        Coupon coupon1 = couponService.getCoupon(1L);

        //then
        assertThat(coupon1).isNotNull();
        assertEquals(coupon.getCode(), coupon1.getCode());
        assertThat(coupon.getDiscountAmount()).isEqualByComparingTo(coupon1.getDiscountAmount());
        assertEquals(coupon.getQuantity(), coupon1.getQuantity());
    }


}