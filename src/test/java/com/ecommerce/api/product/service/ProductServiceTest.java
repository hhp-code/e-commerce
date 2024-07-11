package com.ecommerce.api.product.service;

import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.api.domain.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("Sample ProductRequest", new BigDecimal("100.00"), 50);
        productRepository.save(sampleProduct);
    }

    @Test
    @DisplayName("상품 조회 테스트")
    void testGetProduct() {
        Product result = productService.getProduct(sampleProduct.getId());

        assertNotNull(result);
        assertEquals(sampleProduct.getId(), result.getId());
        assertEquals("Sample ProductRequest", result.getName());
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals(50, result.getAvailableStock());
        assertFalse(result.isDeleted());
    }




    @Test
    @DisplayName("전체 상품 조회 테스트")
    void testGetProducts() {
        List<Product> products = productService.getProducts();

        assertFalse(products.isEmpty());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Sample ProductRequest")));
    }
    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }
}