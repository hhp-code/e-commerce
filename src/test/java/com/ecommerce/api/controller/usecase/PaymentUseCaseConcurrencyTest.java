package com.ecommerce.api.controller.usecase;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.application.UserFacade;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderCommandService;
import com.ecommerce.domain.order.service.OrderQueryService;
import com.ecommerce.application.external.DummyPlatform;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class PaymentUseCaseConcurrencyTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private OrderQueryService orderQueryService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private UserService userService;
    @Autowired
    private OrderCommandService orderCommandService;
    @Autowired
    private ProductService productService;
    @Mock
    private DummyPlatform dummyPlatform;
    @Autowired
    private PaymentUseCase paymentUseCase;
    @Autowired
    private QuantumLockManager quantumLockManager;

    private Product testProduct;
    private List<User> testUsers;
    private List<Order> testOrders;

    @BeforeEach
    void setUp() {
        testProduct = productService.saveAndGet(new Product("test", BigDecimal.valueOf(1), 10));

        testUsers = new ArrayList<>();
        testOrders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = userService.saveUser(new User("testUser" + i, BigDecimal.valueOf(20)));
            testUsers.add(user);
            Map<Product, Integer> orderItem = Map.of(testProduct, 1);
            testOrders.add(orderCommandService.saveOrder(new Order(user, orderItem)));
        }

        when(dummyPlatform.send(any(Order.class))).thenReturn(true);

        paymentUseCase = new PaymentUseCase(orderCommandService,
                quantumLockManager, orderQueryService);
        for(Order order : testOrders) {
            OrderCommand.Create orderCreate = new OrderCommand.Create(order.getId(), Map.of(testProduct.getId(), 1));
            paymentUseCase.orderCommandService.createOrder(orderCreate, paymentUseCase);
        }
    }

    @Test
    void testConcurrentPayments() throws Exception {
        int concurrentRequests = 10;
        CountDownLatch readyLatch = new CountDownLatch(concurrentRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(concurrentRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);
        AtomicInteger successfulPayments = new AtomicInteger(0);

        for (int i = 0; i < concurrentRequests; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    readyLatch.countDown(); // 스레드가 준비되었음을 알림
                    startLatch.await(); // 모든 스레드가 시작 신호를 기다림

                    OrderCommand.Payment orderPay = new OrderCommand.Payment(testOrders.get(index).getId());
                    paymentUseCase.payOrder(orderPay);
                    productService.getProduct(testProduct.getId());
                    successfulPayments.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        readyLatch.await(); // 모든 스레드가 준비될 때까지 대기
        startLatch.countDown(); // 모든 스레드에게 동시에 시작 신호를 보냄
        finishLatch.await(10, TimeUnit.SECONDS); // 모든 스레드가 작업을 완료할 때까지 대기

        // 재고 확인
        Product updatedProduct = productService.getProduct(testProduct.getId());
        assertEquals(10 - successfulPayments.get(), updatedProduct.getStock(),
                "재고는 성공한 결제 수만큼 감소해야 합니다.");

        // 사용자 포인트 및 주문 상태 확인
        for (int i = 0; i < concurrentRequests; i++) {
            User user = userService.getUser(testUsers.get(i).getId());
            Order order = orderQueryService.getOrder(testOrders.get(i).getId());
            if (Objects.equals(order.getOrderStatus(), OrderStatus.ORDERED.name())) {
                assertThat(BigDecimal.TEN ).isEqualByComparingTo(user.getPoint());
            } else {
                assertEquals(BigDecimal.valueOf(20), user.getPoint(), "결제 실패 시 사용자 포인트는 변경되지 않아야 합니다.");
            }
        }

        executorService.shutdown();
    }
}