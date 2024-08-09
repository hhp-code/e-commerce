package com.ecommerce.application.usecase;

import com.ecommerce.domain.event.DomainEventPublisher;
import com.ecommerce.domain.order.event.PayAfterEvent;
import com.ecommerce.infra.event.PaymentEventPublisher;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderCommandService;
import com.ecommerce.domain.order.service.OrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class PaymentUseCase {

    private static final String ORDER_LOCK_PREFIX = "order:";
    private static final Duration ORDER_LOCK_TIMEOUT = Duration.ofSeconds(5);


    public final OrderCommandService orderCommandService;
    private final QuantumLockManager quantumLockManager;
    private final OrderQueryService orderQueryService;
    private final DomainEventPublisher eventPublisher;

    public PaymentUseCase(OrderCommandService orderCommandService, QuantumLockManager quantumLockManager, OrderQueryService orderQueryService, DomainEventPublisher eventPublisher) {
        this.orderCommandService = orderCommandService;
        this.quantumLockManager = quantumLockManager;
        this.orderQueryService = orderQueryService;
        this.eventPublisher = eventPublisher;
    }

    public OrderInfo.Detail payOrder(OrderCommand.Payment command) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(),
                    ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order queryOrder = orderQueryService.getOrder(command.orderId());
                        Order execute = command.execute(queryOrder);
                        Order commandOrder = orderCommandService.saveOrder(execute);
                        eventPublisher.publish(new PayAfterEvent(commandOrder.getId()));
                        return OrderInfo.Detail.from(commandOrder);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 결제 중 락 획득 시간 초과");
        } catch (Exception e) {
            cancelOrder(new OrderCommand.Cancel(command.orderId()));
            throw e;
        }
    }

    public OrderInfo.Detail cancelOrder(OrderCommand.Cancel command) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(), ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order queryOrder = orderQueryService.getOrder(command.orderId());
                        Order execute = command.execute(queryOrder);
                        Order commandOrder = orderCommandService.saveOrder(execute);
                        eventPublisher.publish(new PayAfterEvent(commandOrder.getId()));
                        return OrderInfo.Detail.from(commandOrder);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 취소 중 락 획득 시간 초과");
        }
    }
}
