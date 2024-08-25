package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.event.PayAfterEvent;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.infra.event.PaymentEventPublisher;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class PaymentUseCase {

    private static final String ORDER_LOCK_PREFIX = "order:";
    private static final Duration ORDER_LOCK_TIMEOUT = Duration.ofSeconds(5);


    private final QuantumLockManager quantumLockManager;
    private final OrderService orderQueryService;
    private final PaymentEventPublisher eventPublisher;
    private final UserService userService;

    public PaymentUseCase(QuantumLockManager quantumLockManager, OrderService orderQueryService, PaymentEventPublisher eventPublisher, UserService userService) {
        this.quantumLockManager = quantumLockManager;
        this.orderQueryService = orderQueryService;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
    }

    public OrderInfo.Detail payOrder(OrderCommand.Payment command) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(),
                    ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order queryOrder = orderQueryService.getOrder(command.orderId());
                        Map<Product, Integer> orderItems = queryOrder.getOrderItems();
                        orderItems.forEach(Product::deductStock);
                        User user = userService.getUser(command.userId());
                        user.deductPoint(queryOrder.getTotalAmount());
                        Order commandOrder = orderQueryService.saveOrder(queryOrder);
                        eventPublisher.publish(new PayAfterEvent(commandOrder.getId()));
                        return OrderInfo.Detail.from(commandOrder);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 결제 중 락 획득 시간 초과");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderInfo.Detail cancelOrder(OrderCommand.Cancel command) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(), ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order queryOrder = orderQueryService.getOrder(command.orderId());
                        Order execute = command.execute(queryOrder);
                        Order commandOrder = orderQueryService.saveOrder(execute);
                        return OrderInfo.Detail.from(commandOrder);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 취소 중 락 획득 시간 초과");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
