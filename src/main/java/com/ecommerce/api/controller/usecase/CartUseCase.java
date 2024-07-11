package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;

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
        if (product.getAvailableStock() < command.quantity()) {
            throw new IllegalStateException("상품의 재고가 부족합니다. 상품 ID: " + command.productId());
        }

        OrderItem orderItem = new OrderItem(product, command.quantity());
        order.addCartItem(orderItem);

        return orderService.saveAndGet(order);
    }
}
