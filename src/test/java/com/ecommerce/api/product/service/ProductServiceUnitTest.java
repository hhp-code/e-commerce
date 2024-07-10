package com.ecommerce.api.product.service;

import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = createProduct(1L, "Sample ProductRequest", "10000", 100, LocalDateTime.now());
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class GetProductTests {
        @Test
        @DisplayName("존재하는 상품 ID로 조회 시 상품 반환")
        void testGetExistingProduct() {
            //given
            when(productRepository.getProduct(1L)).thenReturn(Optional.of(sampleProduct));

            //when
            Product result = productService.getProduct(1L);

            //then
            assertNotNull(result);
            assertEquals("Sample ProductRequest", result.getName());
            assertEquals(new BigDecimal("10000"), result.getPrice());
            assertEquals(100, result.getAvailableStock());
            assertFalse(result.isDeleted());

            verify(productRepository, times(1)).getProduct(1L);
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회 시 예외 발생")
        void testGetNonExistingProduct() {
            //given
            when(productRepository.getProduct(999L)).thenThrow(new IllegalArgumentException("Invalid product ID"));

            //when & then
            assertThrows(IllegalArgumentException.class, () -> productService.getProduct(999L));

            verify(productRepository, times(1)).getProduct(999L);
        }
    }

    @Nested
    @DisplayName("인기 상품 조회 테스트")
    class GetPopularProductsTests {
        @Test
        @DisplayName("인기 상품 조회 - 3일 기준")
        void testGetPopularProductsLastThreeDays() {
            // 3일 전 시간
            Instant threeDaysAgo = Instant.now().minus(3, java.time.temporal.ChronoUnit.DAYS);
            List<Product> allProducts = Arrays.asList(
                    createProduct(1L, "Recent ProductRequest 1", "20000", 50, LocalDateTime.now()),
                    createProduct(2L, "Recent ProductRequest 2", "15000", 30, LocalDateTime.now().minusDays(1)),
                    createProduct(3L, "Recent ProductRequest 3", "25000", 20, LocalDateTime.now().minusDays(2)),
                    createProduct(4L, "Old ProductRequest 1", "18000", 40, LocalDateTime.now().minusDays(4)),
                    createProduct(5L, "Old ProductRequest 2", "22000", 25, LocalDateTime.now().minusDays(5))
            );

            //given
            when(productRepository.getPopularProducts()).thenReturn(allProducts);

            //when
            List<Product> result = productService.getPopularProducts();

            //then
            assertNotNull(result);
            assertEquals(3, result.size(), "3일 이내의 상품만 반환되어야 합니다.");
            assertTrue(result.stream().allMatch(p -> p.getLastUpdated().isAfter(threeDaysAgo)),
                    "모든 반환된 상품은 3일 이내에 판매된 것이어야 합니다.");

            verify(productRepository, times(1)).getPopularProducts();
        }

        @Test
        @DisplayName("인기 상품이 없을 경우 빈 리스트 반환")
        void testGetPopularProductsWhenEmpty() {
            when(productRepository.getPopularProducts()).thenReturn(Collections.emptyList());

            List<Product> result = productService.getPopularProducts();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(productRepository, times(1)).getPopularProducts();
        }
    }

    @Nested
    @DisplayName("전체 상품 조회 테스트")
    class GetProductsTests {
        @Test
        @DisplayName("전체 상품 조회")
        void testGetProducts() {
            List<Product> allProducts = Arrays.asList(
                    createProductRequest(1L, "ProductRequest 1", "10000", 100),
                    createProductRequest(2L, "ProductRequest 2", "15000", 80),
                    createProductRequest(3L, "ProductRequest 3", "20000", 60)
            );

            when(productRepository.getProducts()).thenReturn(allProducts);

            List<Product> result = productService.getProducts();

            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals("ProductRequest 1", result.get(0).getName());
            assertEquals("ProductRequest 3", result.get(2).getName());

            verify(productRepository, times(1)).getProducts();
        }

        @Test
        @DisplayName("상품이 없을 경우 빈 리스트 반환")
        void testGetProductsWhenEmpty() {
            when(productRepository.getProducts()).thenReturn(Collections.emptyList());

            List<Product> result = productService.getProducts();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(productRepository, times(1)).getProducts();
        }
    }

    private Product createProductRequest(Long id, String name, String price, Integer availableStock) {
        return createProduct(id, name, price, availableStock, LocalDateTime.now());
    }

    private Product createProduct(Long id, String name, String price, Integer availableStock, LocalDateTime lastUpdated) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setAvailableStock(availableStock);
        product.setReservedStock(0);
        product.setLastUpdated(lastUpdated);
        product.setDeleted(false);
        return product;
    }
}