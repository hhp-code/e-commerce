package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
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

    public Order addItemToOrder(OrderCommand.Add command) {
        return orderService.getOrder(command.orderId())
                .addItem(productService, command.productId(), command.quantity())
                .saveAndGet(orderService);
    }

    public Order deleteItemFromOrder(OrderCommand.Delete orderDeleteItem) {
        return  orderService.getOrder(orderDeleteItem.orderId())
                .deleteItem(productService, orderDeleteItem.productId())
                .saveAndGet(orderService);
    }
}
