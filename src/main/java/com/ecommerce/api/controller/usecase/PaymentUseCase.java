package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentUseCase {
    private final OrderService orderService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;
    private final UserPointService userPointService;
    private final UserService userService;

    public PaymentUseCase(OrderService orderService, ProductService productService, DummyPlatform dummyPlatformUseCase, UserPointService userPointService, UserService userService) {
        this.orderService = orderService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
        this.userPointService = userPointService;
        this.userService = userService;
    }

    public Order payOrder(OrderCommand.Payment orderPay) {
        User user = userService.getUser(orderPay.userId());
        Order order = orderService.getOrder(orderPay.orderId());
        try {
            order.getOrderItems().forEach(productService::deductStock);
            userPointService.deductPoint(user.getId(), order.getTotalAmount());
            orderService.saveAndGet(order).finish();
            boolean externalSystemSuccess = dummyPlatform.send(order);
            if (!externalSystemSuccess) {
                throw new RuntimeException("Failed to send order to external system");
            }

            return order;
        } catch (Exception e) {
            cancelOrder(new OrderCommand.Cancel(user.getId(), order.getId()));
            throw new RuntimeException("Payment processing failed", e);
        }
    }



    public Order cancelOrder(OrderCommand.Cancel orderCancel) {
        Order order = orderService.getOrder(orderCancel.orderId());
        order.getOrderItems().forEach(productService::chargeStock);
        userPointService.chargePoint(orderCancel.userId(), order.getTotalAmount());
        orderService.saveAndGet(order).cancel();
        dummyPlatform.send(order);
        return order;
    }
}
