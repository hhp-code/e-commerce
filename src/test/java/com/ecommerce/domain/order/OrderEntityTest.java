package com.ecommerce.domain.order;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.infra.order.entity.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {
    @Test
    @DisplayName("Order 객체 생성 테스트")
    public void testAddOrderItem() {
        OrderWrite orderEntity = new OrderWrite(new User());
        Product product = new Product("Test Product", BigDecimal.valueOf(100.00),10);
        OrderItemWrite orderItem = new OrderItemWrite(product, 1);
        orderEntity.addOrderItem(orderItem);

        assertEquals(1, orderEntity.getItems().size());
    }

    @Test
    @DisplayName("Order 상품 계산 테스트")
    public void testCalculatePrices() {
        OrderWrite orderEntity = new OrderWrite(new User());
        Product product1 = new Product("Product 1", BigDecimal.valueOf(10.00),10);
        Product product2 = new Product("Product 2", BigDecimal.valueOf(20.00),10);
        OrderItemWrite orderItem1 = new OrderItemWrite(product1, 2);
        OrderItemWrite orderItem2 = new OrderItemWrite(product2, 1);
        orderEntity.addOrderItem(orderItem1);
        orderEntity.addOrderItem(orderItem2);

        orderEntity.calculatePrices();

        assertEquals(BigDecimal.valueOf(40.00), orderEntity.getRegularPrice());
        assertEquals(BigDecimal.valueOf(40.00), orderEntity.getSellingPrice());
    }

    @Test
    @DisplayName("Order 쿠폰 적용 테스트")
    public void testApplyCoupon() {
        OrderWrite orderEntity = new OrderWrite(new User());
        Product product = new Product("Test Product", BigDecimal.valueOf(100.00),10);
        OrderItemWrite orderItem = new OrderItemWrite(product, 1);
        orderEntity.addOrderItem(orderItem);
        orderEntity.calculatePrices();

        Coupon coupon = new Coupon("testcoupon",BigDecimal.valueOf(10),DiscountType.PERCENTAGE, 10);
        orderEntity.applyCoupon(coupon);

        assertEquals(BigDecimal.valueOf(90.00), orderEntity.getSellingPrice());
    }

    @Test
    @DisplayName("Order 상태 변경 테스트")
    public void testOrderStatusChange() {
        OrderWrite orderWrite = new OrderWrite(new User());
        assertEquals("PREPARED", orderWrite.getOrderStatus());

        orderWrite.finish();
        assertEquals("ORDERED", orderWrite.getOrderStatus());

        orderWrite.cancel();
        assertEquals("CANCELLED", orderWrite.getOrderStatus());
    }
}