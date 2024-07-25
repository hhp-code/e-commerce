package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentUseCaseConcurrencyTest {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Mock
    private DummyPlatform dummyPlatform;
    @Autowired
    private UserPointService userPointService;

    @Autowired
    private PaymentUseCase paymentUseCase;


    private User testUser;
    private Order testOrder;
    private Product testProduct;



    @BeforeEach
    void setUp() {
        testUser = new User( "test", BigDecimal.valueOf(1000));
        userService.saveUser(testUser);
        testProduct = new Product( "test", BigDecimal.TEN, 10);
        productService.saveAndGet(testProduct);
        Map<Product,Integer> orderItem= Map.of(testProduct, 1);
        testOrder = new Order(testUser, orderItem);
        orderService.saveAndGet(testOrder);


        when(dummyPlatform.send(any(Order.class))).thenReturn(true);

        paymentUseCase = new PaymentUseCase(orderService, productService, dummyPlatform, userPointService, userService);
    }

    @Test
    @Transactional
    void testConcurrentPayments() throws Exception {
        int concurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        List<Future<Order>> futures = new ArrayList<>();


        for (int i = 0; i < concurrentRequests; i++) {
            futures.add(executor.submit(() -> {
                try {
                    OrderCommand.Payment orderPay = new OrderCommand.Payment(testUser.getId(), testOrder.getId());
                    return paymentUseCase.payOrder(orderPay);
                } catch (Exception e) {
                    System.out.println("Payment failed: " + e.getMessage());
                    return null;
                } finally {
                    latch.countDown();
                }
            }));
        }


        latch.await(10, TimeUnit.SECONDS); // 모든 요청이 완료될 때까지 대기 (최대 10초)

        long successfulOrders = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .count();

        Product updatedProduct = productService.getProduct(testProduct.getId());
        int expectedStock = Math.max(0, 10 - (int)successfulOrders);


        assertEquals(expectedStock, updatedProduct.getStock(),
                "Stock should be decreased by " + successfulOrders);

        executor.shutdown();
    }
}