package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.external.DummyPlatform;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PaymentUseCase {
    private final OrderService orderService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;

    public PaymentUseCase(OrderService orderService, ProductService productService, DummyPlatform dummyPlatformUseCase) {
        this.orderService = orderService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
    }

    public Order payOrder(OrderCommand.Payment orderPay) {
        Order order = orderService.getOrder(orderPay.orderId());
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem item : orderItems) {
            productService.decreaseStock(item.getProduct().getId(), item.getQuantity());
        }
        order.finish();
        dummyPlatform.send(order);
        return order;
    }
}
