package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.usercoupon.UserCoupon;
import com.ecommerce.domain.usercoupon.service.UserCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.stream.IntStream;

public class CouponUseCaseConcurrencyTest {

    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;
    @Mock
    private UserCouponService userCouponService;
    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponUseCase couponUseCase;

    @Mock
    private User mockUser;

    @Mock
    private Order mockOrder;

    @Mock
    private Coupon mockCoupon;

    @Mock
    private UserCoupon mockUserCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        couponUseCase = new CouponUseCase(userService, orderService, userCouponService, couponService);
    }

    @Test
    void testConcurrentCouponUse() {
        // 테스트 설정
        int numberOfUsers = 100;
        Long couponId = 1L;
        int couponQuantity = 10;

        mockCoupon.setQuantity(couponQuantity);

        // Mock 객체 설정
        when(userService.getUser(anyLong())).thenReturn(mockUser);
        when(orderService.getOrder(anyLong())).thenReturn(mockOrder);
        when(couponService.getCoupon(couponId)).thenReturn(mockCoupon);
        when(userCouponService.getUserCoupon(any(), any())).thenReturn(mockUserCoupon);
        when(userCouponService.updateUserCoupon(any())).thenReturn(mockUserCoupon);

        // ExecutorService 생성
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // CompletableFuture 리스트 생성
        List<CompletableFuture<UserCoupon>> futures = IntStream.range(0, numberOfUsers)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return couponUseCase.useCoupon((long) i, couponId);
                    } catch (RuntimeException e) {
                        return null;
                    }
                }, executor))
                .toList();

        // 모든 Future 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 결과 검증
        long successfulUses = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .count();

        assertEquals(couponQuantity, successfulUses, "성공적인 쿠폰 사용 횟수가 쿠폰 수량과 일치해야 합니다.");
        verify(couponService, times(numberOfUsers)).getCoupon(couponId);
        verify(userCouponService, times((int) successfulUses)).updateUserCoupon(any());

        executor.shutdown();
    }
}