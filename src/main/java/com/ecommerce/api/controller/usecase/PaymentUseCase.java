package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.service.UserBalanceService;
import com.ecommerce.external.DummyPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentUseCase {
    private final OrderService orderService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;
    private final UserBalanceService userBalanceService;

    public PaymentUseCase(OrderService orderService, ProductService productService, DummyPlatform dummyPlatformUseCase, UserBalanceService userBalanceService) {
        this.orderService = orderService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
        this.userBalanceService = userBalanceService;
    }

    public Order payOrder(OrderCommand.Payment orderPay) {
        Order order = orderService.getOrder(orderPay.orderId());
        List<OrderItem> orderItems = order.getOrderItems();
        try {
            for (OrderItem item : orderItems) {
                productService.decreaseStock(item.getProduct().getId(), item.getQuantity());
            }
            userBalanceService.decreaseBalance(order.getUser().getId(), order.getTotalAmount());
            orderService.saveAndGet(order).finish();
            boolean externalSystemSuccess = dummyPlatform.send(order);
            if (!externalSystemSuccess) {
                throw new RuntimeException("Failed to send order to external system");
            }

            return order;
        } catch (Exception e) {
            cancelOrder(new OrderCommand.Cancel(order.getId()));
            throw new RuntimeException("Payment processing failed", e);
        }
    }



    public Order cancelOrder(OrderCommand.Cancel orderCancel) {
        Order order = orderService.getOrder(orderCancel.orderId());
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem item : orderItems) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        userBalanceService.increaseBalance(order.getUser().getId(), order.getTotalAmount());
        orderService.saveAndGet(order).cancel();
        dummyPlatform.send(order);
        return order;
    }
}
