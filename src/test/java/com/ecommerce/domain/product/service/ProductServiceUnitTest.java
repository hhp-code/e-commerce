package com.ecommerce.domain.product.service;

import com.ecommerce.domain.product.ProductWrite;
import com.ecommerce.interfaces.exception.domain.ProductException;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @InjectMocks
    private ProductFacade productFacade;

    private ProductWrite sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = createProduct("Sample ProductRequest", "10000", 100, LocalDateTime.now());
    }

    @Test
    @DisplayName("존재하는 상품 ID로 조회 시 상품 반환")
    void testGetExistingProduct() {
        //given

        //when
        ProductWrite result = productService.getProduct(1L);

        //then
        assertNotNull(result);
        assertEquals("Sample ProductRequest", result.getName());
        assertEquals(new BigDecimal("10000"), result.getPrice());
        assertEquals(100, result.getStock());
        assertFalse(result.isDeleted());

        verify(productRepository, times(1)).getProduct(1L);
    }


    @Test
    @DisplayName("인기 상품이 없을 경우 빈 리스트 반환")
    void testGetPopularProductsWhenEmpty() {
        when(productRepository.getPopularProducts()).thenReturn(Collections.emptyList());

        List<ProductWrite> result = productService.getPopularProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository, times(1)).getPopularProducts();
    }

    @Test
    @DisplayName("전체 상품 조회")
    void testGetProducts() {
        List<ProductWrite> allProducts = Arrays.asList(
                createProductRequest("ProductRequest 1", "10000", 100),
                createProductRequest("ProductRequest 2", "15000", 80),
                createProductRequest("ProductRequest 3", "20000", 60)
        );


        List<ProductWrite> result = productService.getProducts();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("ProductRequest 1", result.get(0).getName());
        assertEquals("ProductRequest 3", result.get(2).getName());

        verify(productRepository, times(1)).getProducts();
    }

    @Test
    @DisplayName("상품조회 실패 - 빈 리스트")
    void testGetProductsWhenEmpty() {
        when(productRepository.getProducts()).thenReturn(Collections.emptyList());

        List<ProductWrite> result = productService.getProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository, times(1)).getProducts();
    }

    @Test
    @DisplayName("상품 재고 차감 - 상품 저장실패")
    void testDeductStockWhenSaveFailed() {
        assertThrows(ProductException.ServiceException.class, () -> productFacade.deductStock(sampleProduct, 10));
    }

    @Test
    @DisplayName("상품 재고 충전 - 상품 저장실패")
    void testChargeStockWhenSaveFailed() {
        assertThrows(ProductException.ServiceException.class, () -> productFacade.chargeStock(sampleProduct, 10));
    }

    private ProductWrite createProductRequest(String name, String price, Integer availableStock) {
        return createProduct(name, price, availableStock, LocalDateTime.now());
    }

    private ProductWrite createProduct(String name, String price, Integer availableStock, LocalDateTime lastUpdated) {
        return new ProductWrite(name, new BigDecimal(price), availableStock);
    }
}