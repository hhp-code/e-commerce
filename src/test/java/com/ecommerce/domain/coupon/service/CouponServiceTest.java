package com.ecommerce.domain.coupon.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.domain.coupon.CouponWrite;
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
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@ActiveProfiles("cleanser")
class CouponServiceTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private CouponService couponService;

    CouponCommand.Create create;
    @BeforeEach
    void setUp() {
        couponService.deleteAll();
        create = new CouponCommand.Create(
                "testCoupon",
                BigDecimal.valueOf(1000), 10,
                DiscountType.FIXED_AMOUNT,  LocalDateTime.now(), LocalDateTime.now().plusDays(7),true);
    }


    @Test
    @DisplayName("쿠폰 생성 성공 시나리오")
    void createCoupon() {
        // given //when
        CouponWrite coupon = couponService.createCoupon(create);

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
        CouponWrite coupon1 = couponService.createCoupon(create);

        //when
        CouponWrite coupon = couponService.getCoupon(1L);

        //then
        assertThat(coupon1).isNotNull();
        assertEquals(coupon.getCode(), coupon1.getCode());
        assertThat(coupon.getDiscountAmount()).isEqualByComparingTo(coupon1.getDiscountAmount());
        assertEquals(coupon.getQuantity(), coupon1.getQuantity());
    }


}