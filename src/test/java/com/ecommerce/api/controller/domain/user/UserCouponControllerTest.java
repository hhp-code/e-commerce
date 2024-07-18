package com.ecommerce.api.controller.domain.user;

import com.ecommerce.api.controller.usecase.CouponUseCase;
import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponCommand;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserCouponService;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
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
    @DisplayName("사용자에게 쿠폰 발급 - 성공")
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
    @DisplayName("발급된 쿠폰들 조회- 성공")
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
    @DisplayName("쿠폰 사용 - 성공")
    void testUseCoupon() throws Exception {
        Long userId = 1L;
        Long userCouponId = 1L;

        when(couponUseCase.useCoupon(userId, userCouponId)).thenReturn(testUser);

        mockMvc.perform(post("/api/users/{userId}/coupons/{userCouponId}/use", userId, userCouponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupons[0].code").value("SUMMER2024"))
                .andExpect(jsonPath("$.coupons[0].active").value(true));
    }
    @Test
    @DisplayName("쿠폰 발급 요청 상태 확인 - 성공")
    void testCheckCouponIssueStatus() throws Exception {
        Long userId = 1L;
        CouponCommand.Issue issue= new CouponCommand.Issue(userId, 1L, CouponCommand.Issue.Status.COMPLETED, Instant.now());
        when(couponQueueManager.checkStatus(userId)).thenReturn(issue);

        mockMvc.perform(get("/api/users/{userId}/coupon/status", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponQueueStatus").value("COMPLETED"));
    }

}
