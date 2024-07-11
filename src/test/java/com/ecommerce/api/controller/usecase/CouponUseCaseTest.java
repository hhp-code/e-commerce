package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.usercoupon.UserCoupon;
import com.ecommerce.domain.usercoupon.service.UserCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponUseCaseTest {
    @Mock
    private CouponService couponService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private UserCouponService userCouponService;

    private CouponUseCase couponUseCase;

    @BeforeEach
    void setUp() {
        couponUseCase = new CouponUseCase(userService, orderService,  userCouponService, couponService);
    }

    @Nested
    @DisplayName("useCoupon 메서드")
    class UseCouponTest {
        @Test
        @DisplayName("쿠폰 사용 성공")
        void useCouponSuccess() {
            // Given
            Long userId = 1L;
            Long couponId = 1L;
            User user = new User("test", BigDecimal.ZERO);
            Coupon coupon = new Coupon("CODE123", BigDecimal.TEN, DiscountType.FIXED_AMOUNT, 100,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(30), true);
            UserCoupon userCoupon = new UserCoupon(user, coupon);

            Product product = new Product("testProduct", new BigDecimal("100.00"), 10);
            OrderItem orderItem = new OrderItem(product, 1);
            List<OrderItem> orderItems = List.of(orderItem);

            Order order = new Order(user, orderItems);

            when(userService.getUser(userId)).thenReturn(user);
            when(couponService.getCoupon(couponId)).thenReturn(coupon);
            when(userCouponService.getUserCoupon(user, coupon)).thenReturn(userCoupon);
            when(userCouponService.updateUserCoupon(userCoupon)).thenReturn(userCoupon);
            when(orderService.getOrder(userId)).thenReturn(order);

            // When
            UserCoupon result = couponUseCase.useCoupon(userId, couponId);

            // Then
            assertNotNull(result);
            assertTrue(result.isUsed());
            verify(userCouponService).updateUserCoupon(userCoupon);

            assertNotNull(order.getSellingPrice());
            assertEquals(new BigDecimal("90.00"), order.getSellingPrice());

            order.applyCoupon(coupon);
            assertEquals(new BigDecimal("80.00"), order.getSellingPrice());
        }

        @Test
        @DisplayName("이미 사용된 쿠폰 사용 시도")
        void useCouponAlreadyUsed() {
            // Given
            Long userId = 1L;
            Long couponId = 1L;
            User user = new User();
            Coupon coupon = new Coupon();
            UserCoupon userCoupon = new UserCoupon(user, coupon);
            userCoupon.use(); // 쿠폰을 이미 사용 상태로 설정


            // When & Then
            assertThrows(RuntimeException.class, () -> couponUseCase.useCoupon(userId, couponId));
        }
    }
}