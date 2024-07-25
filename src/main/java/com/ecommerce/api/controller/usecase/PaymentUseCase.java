package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserPointService;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class PaymentUseCase {
    private final OrderService orderService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;
    private final UserPointService userPointService;
    private final UserService userService;
    private final QuantumLockManager quantumLockManager;

    public PaymentUseCase(OrderService orderService, ProductService productService, DummyPlatform dummyPlatformUseCase, UserPointService userPointService, UserService userService, QuantumLockManager quantumLockManager) {
        this.orderService = orderService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
        this.userPointService = userPointService;
        this.userService = userService;
        this.quantumLockManager = quantumLockManager;
    }

    public Order payOrder(OrderCommand.Payment orderPay) {
        String lockKey = "order:" + orderPay.orderId();
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                User user = userService.getUser(orderPay.userId());
                Order order = orderService.getOrder(orderPay.orderId());
                try {
                    order.getOrderItems().forEach(productService::deductStock);
                    userPointService.deductPoint(user.getId(), order.getTotalAmount());
                    dummyPlatform.send(order);
                    order.finish();
                    return orderService.saveAndGet(order);
                } catch (Exception  e) {
                    cancelOrder(new OrderCommand.Cancel(user.getId(), order.getId()));
                    throw e;
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException("Lock acquisition timeout", e);
        }
    }


    public Order cancelOrder(OrderCommand.Cancel orderCancel) {
        User user = userService.getUser(orderCancel.userId());
        Order order = orderService.getOrder(orderCancel.orderId());
        order.getOrderItems().forEach(productService::chargeStock);
        userPointService.chargePoint(user.getId(), order.getTotalAmount());
        orderService.saveAndGet(order).cancel();
        dummyPlatform.send(order);
        return order;
    }
}
