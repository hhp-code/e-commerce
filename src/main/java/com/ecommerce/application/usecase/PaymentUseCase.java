package com.ecommerce.application.usecase;

import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderCommandService;
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


    private final OrderCommandService orderCommandService;
    private final ProductService productService;
    private final DummyPlatform dummyPlatform;
    private final UserService userService;
    private final QuantumLockManager quantumLockManager;

    public PaymentUseCase(OrderCommandService orderCommandService, ProductService productService, DummyPlatform dummyPlatformUseCase, UserService userService, QuantumLockManager quantumLockManager) {
        this.orderCommandService = orderCommandService;
        this.productService = productService;
        this.dummyPlatform = dummyPlatformUseCase;
        this.userService = userService;
        this.quantumLockManager = quantumLockManager;
    }

    public OrderInfo.Detail payOrder(OrderCommand.Payment command) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(),
                    ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order execute = command.execute(orderCommandService, dummyPlatform);
                        return OrderInfo.Detail.from(execute);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 결제 중 락 획득 시간 초과");
        }
    }
    public OrderInfo.Summary createOrder(OrderCommand.Create command) {
        try{
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.userId(), ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order execute = command.execute(userService, productService);
                        return OrderInfo.Summary.from(execute);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 생성 중 락 획득 시간 초과");
        }
    }

    public OrderInfo.Detail cancelOrder(OrderCommand.Cancel command) {
        try{
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(), ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order execute = command.execute(orderCommandService, dummyPlatform);
                        return OrderInfo.Detail.from(execute);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 취소 중 락 획득 시간 초과");
        }
    }
}
