package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private TransactionTemplate transactionTemplate;

    private User testUser;
    private Order testOrder;
    private Product testProduct;


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();
        testUser = new User(1L, "test", BigDecimal.valueOf(1000));
        userService.saveUser(testUser);
        testProduct = new Product(1L, "test", BigDecimal.TEN, 10);
        productService.saveAndGet(testProduct);
        OrderItem testOrderItem = new OrderItem(testProduct, 1);
        testOrder = new Order(testUser, List.of(testOrderItem));
        Order order = orderService.saveAndGet(testOrder);
        System.out.println(order.getOrderItems().getFirst().getProduct().getAvailableStock()+"stock");


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


        assertEquals(expectedStock, updatedProduct.getAvailableStock(),
                "Stock should be decreased by " + successfulOrders);

        executor.shutdown();
    }
}