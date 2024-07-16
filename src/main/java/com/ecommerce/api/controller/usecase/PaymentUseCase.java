package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserBalanceCommand;
import com.ecommerce.domain.user.service.UserBalanceService;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentUseCase {
    private final OrderService orderService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;
    private final UserBalanceService userBalanceService;
    private final UserService userService;

    public PaymentUseCase(OrderService orderService, ProductService productService, DummyPlatform dummyPlatformUseCase, UserBalanceService userBalanceService, UserService userService) {
        this.orderService = orderService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
        this.userBalanceService = userBalanceService;
        this.userService = userService;
    }

    public Order payOrder(OrderCommand.Payment orderPay) {
        User user = userService.getUser(orderPay.userId());
        System.out.println("orderId"+orderPay.orderId());
        Order order = orderService.getOrder(orderPay.orderId());
        System.out.println(order.getOrderItems().getFirst().getProduct().getAvailableStock()+"stock");
        List<OrderItem> orderItems = order.getOrderItems();
        try {
            for (OrderItem item : orderItems) {
                System.out.println("product00: " + item.getProduct());
                productService.decreaseStock(item.getProduct(), item.getQuantity());
            }
            userBalanceService.decreaseBalance(order.getUser(), order.getTotalAmount());
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
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem item : orderItems) {
            productService.increaseStock(item.getProduct(), item.getQuantity());
        }
        UserBalanceCommand.Create request = new UserBalanceCommand.Create(order.getUser().getId(), order.getTotalAmount());
        userBalanceService.chargeBalance(request);
        orderService.saveAndGet(order).cancel();
        dummyPlatform.send(order);
        return order;
    }
}
