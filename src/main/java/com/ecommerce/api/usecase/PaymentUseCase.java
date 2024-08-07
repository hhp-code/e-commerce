package com.ecommerce.api.usecase;

import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import com.ecommerce.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class PaymentUseCase {

    private static final String ORDER_LOCK_PREFIX = "order:";
    private static final Duration ORDER_LOCK_TIMEOUT = Duration.ofSeconds(5);


    private final OrderService orderService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;
    private final UserService userService;
    private final QuantumLockManager quantumLockManager;

    public PaymentUseCase(OrderService orderService, ProductService productService, DummyPlatform dummyPlatformUseCase, UserService userService, QuantumLockManager quantumLockManager) {
        this.orderService = orderService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
        this.userService = userService;
        this.quantumLockManager = quantumLockManager;
    }

    public Order payOrder(OrderCommand.Payment orderPay) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + orderPay.orderId(),
                    ORDER_LOCK_TIMEOUT,
                    () -> payOrderInternal(orderPay));
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 결제 중 락 획득 시간 초과");
        }


    }
    public Order createOrder(OrderCommand.Create command) {
        try{
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.userId(), ORDER_LOCK_TIMEOUT,
                    () -> createOrderInternal(command));
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 생성 중 락 획득 시간 초과");
        }
    }

    private Order createOrderInternal(OrderCommand.Create command) {
        return new Order().putUser(userService,command.userId())
                .addItems(productService, command.items())
                .saveAndGet(orderService);
    }

    private Order payOrderInternal(OrderCommand.Payment orderPay) {
        try {
            return orderService.getOrder(orderPay.orderId())
                    .deductStock()
                    .deductPoint()
                    .finish()
                    .send(dummyPlatform)
                    .saveAndGet(orderService);
        } catch (Exception e) {
            cancelOrder(new OrderCommand.Cancel(orderPay.orderId()));
            throw new RuntimeException("주문 결제 중 오류 발생", e);
        }
    }


    public Order cancelOrder(OrderCommand.Cancel orderCancel) {
        return orderService.getOrder(orderCancel.orderId())
                .chargeStock()
                .chargePoint()
                .cancel()
                .send(dummyPlatform)
                .saveAndGet(orderService);
    }
}
