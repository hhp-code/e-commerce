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
        sampleProduct = new Product();
        sampleProduct.setId(31L);
        sampleProduct.setName("Sample ProductRequest");
        sampleProduct.setPrice(new BigDecimal("100.00"));
        sampleProduct.setAvailableStock(50);
        sampleProduct.setReservedStock(0);
        sampleProduct.setLastUpdated(LocalDateTime.now());
        sampleProduct.setDeleted(false);

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
    @DisplayName("인기 상품 조회 테스트 (실패 예상)")
    void testGetPopularProducts() {
        Product oldProduct = new Product();
        oldProduct.setName("Old ProductRequest");
        oldProduct.setPrice(new BigDecimal("50.00"));
        oldProduct.setAvailableStock(30);
        oldProduct.setLastUpdated(LocalDateTime.now().minusDays(4));
        productRepository.save(oldProduct);

        List<Product> popularProducts = productService.getPopularProducts();

        // 이 테스트는 실패할 것입니다. 현재 구현은 3일 기준을 고려하지 않기 때문입니다.
        assertFalse(popularProducts.isEmpty());
        assertTrue(popularProducts.stream()
                        .allMatch(p -> p.getLastUpdated().isAfter(Instant.now().minus(3, ChronoUnit.DAYS))),
                "모든 인기 상품은 3일 이내에 업데이트되어야 합니다.");
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