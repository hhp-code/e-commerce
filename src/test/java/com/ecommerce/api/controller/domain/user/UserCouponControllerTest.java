package com.ecommerce.api.controller.domain.user;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCouponController.class)
public class UserCouponControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CouponQueueManager couponQueueManager;

    @MockBean
    private CouponUseCase couponUseCase;

    private final Coupon testCoupon = new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
    private final User testUser= new User(1L, "test", BigDecimal.ZERO, List.of(testCoupon));

    @Test
    void testIssueCouponToUser() throws Exception {
        CompletableFuture<User>  test = CompletableFuture.completedFuture(testUser);
        Long userId = 1L;
        Long couponId = 1L;
        when(couponQueueManager.addToQueueAsync(any(CouponCommand.Issue.class))).thenReturn(test);

        mockMvc.perform(post("/api/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.coupons[0].code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupons[0].discountAmount").value(5000));
    }

    @Test
    void testGetUserCoupons() throws Exception {
        long userId = 1L;
        List<Coupon> userCoupons = Arrays.asList(
                        new Coupon(1L, "SUMMER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true),
                        new Coupon(2L, "WELCOME", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 100, LocalDateTime.now(), LocalDateTime.now().plusDays(30), true)
        );

        when(userService.getUserCoupons(userId)).thenReturn(userCoupons);

        mockMvc.perform(get("/api/users/{userId}/coupons", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("SUMMER2024"))
                .andExpect(jsonPath("$[1].code").value("WELCOME"));
    }

    @Test
    void testUseCoupon() throws Exception {
        Long userId = 1L;
        Long userCouponId = 1L;

        when(couponUseCase.useCoupon(userId, userCouponId)).thenReturn(testUser);

        mockMvc.perform(post("/api/users/{userId}/coupons/{userCouponId}/use", userId, userCouponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupons[0].code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupons[0].active").value(true));
    }

}
