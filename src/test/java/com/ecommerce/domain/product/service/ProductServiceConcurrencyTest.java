package com.ecommerce.domain.product.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.ProductFacade;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class ProductServiceConcurrencyTest {

    @Autowired
    ProductService productService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private ProductFacade productFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    UserService userService;


    private Product testProduct;

    @BeforeEach
    void setUp() {
        Product sample = new Product("Test Product", BigDecimal.valueOf(100), 10);
        testProduct = productService.saveAndGet(sample);

    }

    @Test
    @DisplayName("상품 재고 차감 테스트")
    void testDeductStock() {
        int taskCount = 10;
        int deductAmount = 1;

        List<CompletableFuture<Void>> futures = IntStream.range(0, taskCount)
                .mapToObj(i -> CompletableFuture.runAsync(() ->
                        productFacade.deductStock(testProduct, deductAmount)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        assertThat(productService.getProduct(testProduct.getId()).getStock()).isEqualTo(0);

    }
    @Test
    @DisplayName("상품 재고 증가 테스트")
    void testChargeStock() {
        int taskCount = 10;
        int quantity = 1;

        List<CompletableFuture<Void>> futures = IntStream.range(0, taskCount)
                .mapToObj(i -> CompletableFuture.runAsync(() ->
                        productFacade.chargeStock(testProduct, quantity)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        assertThat(productService.getProduct(testProduct.getId()).getStock()).isEqualTo(20);
    }

}