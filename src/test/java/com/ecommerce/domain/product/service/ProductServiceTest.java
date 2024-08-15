package com.ecommerce.domain.product.service;

import com.ecommerce.config.DatabaseCleanUp;
import com.ecommerce.domain.product.ProductWrite;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test","cleanser"})
@Transactional
class ProductServiceTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private ProductService productService;

    private ProductWrite sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductWrite("Sample ProductRequest", new BigDecimal("100.00"), 50);
        productService.saveAndGet(sampleProduct);
    }

    @Test
    @DisplayName("상품 조회 테스트")
    void testGetProduct() {
        ProductWrite result = productService.getProduct(sampleProduct.getId());

        assertNotNull(result);
        assertEquals(sampleProduct.getId(), result.getId());
        assertEquals("Sample ProductRequest", result.getName());
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals(50, result.getStock());
        assertFalse(result.isDeleted());
    }

    @Test
    @DisplayName("전체 상품 조회 테스트")
    void testGetProducts() {
        List<ProductWrite> products = productService.getProducts();

        assertFalse(products.isEmpty());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Sample ProductRequest")));
    }
}