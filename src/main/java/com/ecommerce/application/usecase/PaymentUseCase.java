package com.ecommerce.application.usecase;

import com.ecommerce.application.ProductFacade;
import com.ecommerce.application.UserFacade;
import com.ecommerce.domain.order.event.PayAfterEvent;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.service.UserService;
import com.ecommerce.infra.event.PaymentEventPublisher;
import com.ecommerce.config.RedisLockManager;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class PaymentUseCase {

    private static final String ORDER_LOCK_PREFIX = "order:";
    private static final Duration ORDER_LOCK_TIMEOUT = Duration.ofSeconds(10);


    private final RedisLockManager redisLockManager;
    private final OrderService orderQueryService;
    private final PaymentEventPublisher eventPublisher;
    private final ProductFacade productFacade;
    private final UserFacade userFacade;

    public PaymentUseCase(RedisLockManager redisLockManager, OrderService orderQueryService, PaymentEventPublisher eventPublisher, UserService userService, com.ecommerce.application.ProductFacade productFacade, UserFacade userFacade) {
        this.redisLockManager = redisLockManager;
        this.orderQueryService = orderQueryService;
        this.eventPublisher = eventPublisher;
        this.productFacade = productFacade;
        this.userFacade = userFacade;
    }

    public OrderInfo.Detail payOrder(OrderCommand.Payment command) {
        try {
            return redisLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(),
                    ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order queryOrder = orderQueryService.getOrder(command.orderId());
                        Map<Product, Integer> orderItems = queryOrder.getOrderItems();
                        for(Product product : orderItems.keySet()) {
                            productFacade.deductStock(product, orderItems.get(product));
                        }
                        userFacade.deductPoint(command.userId(), queryOrder.getTotalAmount());
                        Order commandOrder = orderQueryService.saveOrder(queryOrder);
                        eventPublisher.publish(new PayAfterEvent(commandOrder.getId()));
                        return OrderInfo.Detail.from(commandOrder);
                    });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderInfo.Detail cancelOrder(OrderCommand.Cancel command) {
        try {
            return redisLockManager.executeWithLock(
                    ORDER_LOCK_PREFIX + command.orderId(), ORDER_LOCK_TIMEOUT,
                    () -> {
                        Order queryOrder = orderQueryService.getOrder(command.orderId());
                        Map<Product, Integer> orderItems = queryOrder.getOrderItems();
                        for(Product product : orderItems.keySet()) {
                            productFacade.chargeStock(product, orderItems.get(product));
                        }
                        userFacade.chargePoint(command.userId(), queryOrder.getTotalAmount());
                        Order execute = command.execute(queryOrder);
                        Order commandOrder = orderQueryService.saveOrder(execute);
                        return OrderInfo.Detail.from(commandOrder);
                    });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
