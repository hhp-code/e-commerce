package com.ecommerce.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.ecommerce.api.exception.domain.ProductException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", BigDecimal.valueOf(100), 10);
    }


    @Test
    @DisplayName("재고 차감 테스트")
    void testDeductStock() {
        product.deductStock(5);
        assertEquals(5, product.getStock());
    }

    @Test
    @DisplayName("재고 차감 실패 테스트")
    void testDeductStockInsufficientStock() {
        assertThrows(ProductException.class, () -> product.deductStock(15));
    }

    @Test
    @DisplayName("재고 추가 테스트")
    void testChargeStock() {
        product.chargeStock(5);
        assertEquals(15, product.getStock());
    }

    @Test
    @DisplayName("재고 추가 실패 테스트")
    void testChargeStockNegativeQuantity() {
        assertThrows(ProductException.class, () -> product.chargeStock(-5));
    }

    @Test
    @DisplayName("최근 업데이트 시간 테스트")
    void testLastUpdated() {
        LocalDateTime initialLastUpdated = product.getLastUpdated();
        product.chargeStock(1);
        assertTrue(product.getLastUpdated().isAfter(initialLastUpdated));
    }

}