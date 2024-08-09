package com.ecommerce.application.usecase;

import com.ecommerce.domain.event.DomainEventPublisher;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.event.OrderPayAfterEvent;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.command.OrderCommandService;
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
    private final DomainEventPublisher eventPublisher;

    public PaymentUseCase(OrderCommandService orderCommandService, QuantumLockManager quantumLockManager, DomainEventPublisher eventPublisher) {
        this.orderCommandService = orderCommandService;
        this.quantumLockManager = quantumLockManager;
        this.eventPublisher = eventPublisher;
    }

    public OrderInfo.Detail payOrder(OrderCommand.Payment command) {
        try {
            return quantumLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(),
                    ORDER_LOCK_TIMEOUT,
                    () -> {
                        OrderWrite queryOrderEntity = orderCommandService.getOrder(command.orderId());
                        OrderWrite execute = command.execute(queryOrderEntity);
                        OrderWrite commandOrderEntity = orderCommandService.saveOrder(execute);
                        eventPublisher.publish(new OrderPayAfterEvent(commandOrderEntity.getId()));
                        return OrderInfo.Detail.from(commandOrderEntity);
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
                        OrderWrite queryOrderEntity = orderCommandService.getOrder(command.orderId());
                        OrderWrite execute = command.execute(queryOrderEntity);
                        OrderWrite commandOrderEntity = orderCommandService.saveOrder(execute);
                        eventPublisher.publish(new OrderPayAfterEvent(commandOrderEntity.getId()));
                        return OrderInfo.Detail.from(commandOrderEntity);
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 취소 중 락 획득 시간 초과");
        }
    }
}
