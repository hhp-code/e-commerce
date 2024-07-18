package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class CartUseCase {
    private final OrderService orderService;
    private final ProductService productService;

    public CartUseCase(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    public Order addCartItemToOrder(OrderCommand.Add command) {
        Order order = orderService.getOrCreateOrder(command);
        Product product = productService.getProduct(command.productId());
        if (product.getStock() < command.quantity()) {
            throw new IllegalStateException("상품의 재고가 부족합니다. 상품 ID: " + command.productId());
        }
        order.addOrderItem(product, command.quantity());
        return orderService.saveAndGet(order);
    }

    public Order deleteCartItemToOrder(OrderCommand.Delete orderDeleteItem) {
        Order order = orderService.getOrder(orderDeleteItem.orderId());
        Product product = productService.getProduct(orderDeleteItem.productId());
        order.deleteOrderItem(product);
        return orderService.saveAndGet(order);
    }
}
