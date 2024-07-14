package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.external.DummyPlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentUseCaseConcurrencyTest {

    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;
    @Mock
    private DummyPlatform dummyPlatform;
    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @Mock
    private Order mockOrder;

    @Mock
    private OrderItem mockOrderItem;

    @Mock
    private User mockUser;

    @Mock
    private Product mockProduct;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentUseCase = new PaymentUseCase(orderService, productService, dummyPlatform, userService);
    }

    @Test
    void testConcurrentPayments() {
        // 테스트 데이터 준비
        Long orderId = 1L;
        Long productId = 1L;
        int initialStock = 10;
        int concurrentRequests = 5;


        when(orderService.getOrder(orderId)).thenReturn(mockOrder);
        when(mockOrder.getOrderItems()).thenReturn(List.of(mockOrderItem));
        when(mockOrderItem.getProduct()).thenReturn(mockProduct);
        when(mockOrder.getUser()).thenReturn(mockUser);
        when(mockProduct.getId()).thenReturn(productId);
        when(mockOrderItem.getQuantity()).thenReturn(1);

        AtomicInteger stockCounter = new AtomicInteger(initialStock);
        doAnswer(invocation -> {
            Long pId = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            if (pId.equals(productId)) {
                return stockCounter.addAndGet(-quantity);
            }
            return 0;
        }).when(productService).decreaseStock(eq(productId), anyInt());

        // 동시 요청 시뮬레이션
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        List<CompletableFuture<Order>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentRequests; i++) {
            CompletableFuture<Order> future = CompletableFuture.supplyAsync(() -> {
                OrderCommand.Payment orderPay = new OrderCommand.Payment(orderId, BigDecimal.ONE);
                return paymentUseCase.payOrder(orderPay);
            }, executor);
            futures.add(future);
        }

        // 모든 요청이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 결과 검증
        verify(orderService, times(concurrentRequests)).getOrder(orderId);
        verify(productService, times(concurrentRequests)).decreaseStock(eq(productId), eq(1));
        verify(mockOrder, times(concurrentRequests)).finish();
        verify(dummyPlatform, times(concurrentRequests)).send(mockOrder);

        assertEquals(initialStock - concurrentRequests, stockCounter.get());
    }
}