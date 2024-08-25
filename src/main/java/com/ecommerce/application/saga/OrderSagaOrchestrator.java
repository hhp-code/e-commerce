package com.ecommerce.application.saga;

import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.event.OrderCancelEvent;
import com.ecommerce.domain.order.event.OrderPayAfterEvent;
import com.ecommerce.domain.order.event.OrderPayEvent;
import com.ecommerce.domain.order.event.OrderQueryEvent;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.product.event.StockChargeEvent;
import com.ecommerce.domain.product.event.StockDeductEvent;
import com.ecommerce.domain.user.event.PointChargeEvent;
import com.ecommerce.domain.user.event.PointDeductEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class OrderSagaOrchestrator {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OrderStateMachine stateMachine;
    private final DynamicTopicListener dynamicTopicListener;
    public OrderSagaOrchestrator(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, OrderStateMachine stateMachine, DynamicTopicListener dynamicTopicListener) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.stateMachine = stateMachine;
        this.dynamicTopicListener = dynamicTopicListener;
    }

    public void startOrderSaga(OrderPayEvent event) {
        try {
            // 주문 정보 조회
            OrderWrite orderInfo = queryOrder(event.orderId());
            List<OrderItemWrite> items = orderInfo.getItems();
            stateMachine.initiate(event.orderId());

            // 재고 차감
            for(OrderItemWrite item : items) {
                kafkaTemplate.send("stock-deduct", objectMapper.writeValueAsString(new StockDeductEvent(item.product().getId(), item.quantity())));
            }
            stateMachine.transitionTo(OrderState.STOCK_DEDUCTED);

            // 포인트 차감
            kafkaTemplate.send("point-deduct", objectMapper.writeValueAsString(new PointDeductEvent(orderInfo.getUserId(), orderInfo.getTotalAmount())));
            stateMachine.transitionTo(OrderState.POINT_DEDUCTED);

            // 주문 완료
            kafkaTemplate.send("order-pay-after", objectMapper.writeValueAsString(new OrderPayAfterEvent(event.orderId())));
            stateMachine.transitionTo(OrderState.COMPLETED);
        } catch (Exception e) {
            handleSagaFailure(event.orderId());
        }
    }

    private OrderWrite queryOrder(Long orderId) throws Exception {
        String responseTopic = "order-query-result-" + UUID.randomUUID().toString();
        CompletableFuture<OrderWrite> future = new CompletableFuture<>();

        dynamicTopicListener.registerFuture(responseTopic, future);

        // 주문 조회 이벤트 발행
        kafkaTemplate.send("order-query", objectMapper.writeValueAsString(new OrderQueryEvent(orderId, responseTopic)));

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("주문 조회 시간 초과", e);
        } finally {
            dynamicTopicListener.registerFuture(responseTopic, null);
        }
    }


    private void handleSagaFailure(Long orderId) {
        try {
            OrderWrite orderInfo = queryOrder(orderId);
            List<OrderItemWrite> items = orderInfo.getItems();

            switch (stateMachine.getCurrentState()) {
                case STOCK_DEDUCTED:
                    // 재고 복구
                    for(OrderItemWrite item : items) {
                        kafkaTemplate.send("stock-charge", objectMapper.writeValueAsString(new StockChargeEvent(item.product().getId(), item.quantity())));
                    }
                    break;
                case POINT_DEDUCTED:
                    // 재고 복구
                    for(OrderItemWrite item : items) {
                        kafkaTemplate.send("stock-charge", objectMapper.writeValueAsString(new StockChargeEvent(item.product().getId(), item.quantity())));
                    }
                    // 포인트 복구
                    kafkaTemplate.send("point-charge", objectMapper.writeValueAsString(new PointChargeEvent(orderInfo.getUserId(), orderInfo.getTotalAmount())));
                    break;
            }
            kafkaTemplate.send("order-cancel", objectMapper.writeValueAsString(new OrderCancelEvent(orderId)));
            stateMachine.transitionTo(OrderState.CANCELLED);
        } catch (Exception ex) {
            log.error("보상 트랜잭션 실패", ex);
        }
    }

}

