package com.ecommerce.domain.order;

import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    @Test
    @DisplayName("Order 객체 생성 테스트")
    public void testAddOrderItem() {
        Order order = new Order(new User());
        Product product = new Product("Test Product", BigDecimal.valueOf(100.00),10);
        order.addOrderItem(product, 2);

        assertEquals(1, order.getOrderItems().size());
        assertEquals(2, order.getOrderItems().get(product));
    }

    @Test
    @DisplayName("Order 상품 계산 테스트")
    public void testCalculatePrices() {
        Order order = new Order(new User());
        Product product1 = new Product("Product 1", BigDecimal.valueOf(10.00),10);
        Product product2 = new Product("Product 2", BigDecimal.valueOf(20.00),10);
        order.addOrderItem(product1, 2);
        order.addOrderItem(product2, 1);

        order.calculatePrices();

        assertEquals(BigDecimal.valueOf(40.00), order.getRegularPrice());
        assertEquals(BigDecimal.valueOf(40.00), order.getSellingPrice());
    }



    @Test
    @DisplayName("Order 상태 변경 테스트")
    public void testOrderStatusChange() {
        Order order = new Order(new User());
        assertEquals("PREPARED", order.getOrderStatus());

        order.finish();
        assertEquals("ORDERED", order.getOrderStatus());

        order.cancel();
        assertEquals("CANCELLED", order.getOrderStatus());
    }
}