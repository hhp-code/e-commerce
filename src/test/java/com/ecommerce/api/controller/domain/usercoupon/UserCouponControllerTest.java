package com.ecommerce.api.controller.domain.usercoupon;

import com.ecommerce.api.controller.domain.usercoupon.dto.UserCouponDto;
import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.usercoupon.service.UserCouponCommand;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.usercoupon.UserCoupon;
import com.ecommerce.domain.usercoupon.service.UserCouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserCouponController.class)
public class UserCouponControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserCouponService userCouponService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CouponUseCase couponUseCase;
    @Test
    void testIssueCouponToUser() throws Exception {
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponDto.UserCouponRequest request = new UserCouponDto.UserCouponRequest(couponId);
        UserCoupon userCoupon = new UserCoupon(new User(userId, "test", BigDecimal.ZERO),
                new Coupon(couponId, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true));

        when(userCouponService.issueCouponToUser(any(UserCouponCommand.UserCouponCreate.class))).thenReturn(userCoupon);

        mockMvc.perform(post("/api/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.coupon.id").value(couponId))
                .andExpect(jsonPath("$.coupon.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupon.discountAmount").value(5000));
    }

    @Test
    void testGetUserCoupons() throws Exception {
        long userId = 1L;
        List<UserCoupon> userCoupons = Arrays.asList(
                new UserCoupon(new User(userId, "test", BigDecimal.ZERO),
                        new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true)),
                new UserCoupon(new User(userId, "test", BigDecimal.ZERO),
                        new Coupon(2L, "WELCOME", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true))
        );

        when(userCouponService.getUserCoupons(userId)).thenReturn(userCoupons);

        mockMvc.perform(get("/api/users/{userId}/coupons", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].coupon.code").value("SUMMER2024"))
                .andExpect(jsonPath("$[1].coupon.code").value("WELCOME"));
    }
    @Test
    void testUseCoupon() throws Exception {
        Long userId = 1L;
        Long userCouponId = 1L;
        UserCoupon usedCoupon = new UserCoupon(
                new User(userId, "test", BigDecimal.ZERO),
                new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true)
        );

        when(couponUseCase.useCoupon(userId, userCouponId)).thenReturn(usedCoupon);

        mockMvc.perform(post("/api/users/{userId}/coupons/{userCouponId}/use", userId, userCouponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon.id").value(1L))
                .andExpect(jsonPath("$.coupon.code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupon.active").value(true));
    }

}
