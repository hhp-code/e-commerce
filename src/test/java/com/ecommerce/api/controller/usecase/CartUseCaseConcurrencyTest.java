package com.ecommerce.api.controller.usecase;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartUseCaseConcurrencyTest {

    @Autowired
    private CartUseCase cartUseCase;

    @Autowired
    private ProductService productService;

    private Product testProduct;
    private User testUser;
    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testUser", BigDecimal.valueOf(1000));

        testProduct = new Product(1L,"testProduct", BigDecimal.valueOf(1000), 10);
    }

    @Test
    void addCartItemToOrder_ConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 테스트를 위한 상품 준비
        productService.saveAndGet(testProduct);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    OrderCommand.Add command = new OrderCommand.Add(testUser.getId(), testProduct.getId(), 1);
                    cartUseCase.addCartItemToOrder(command);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertEquals(5, successCount.get());
        assertEquals(5, failCount.get());

        Product updatedProduct = productService.getProduct(testProduct.getId());
        assertEquals(0, updatedProduct.getAvailableStock());
    }
}