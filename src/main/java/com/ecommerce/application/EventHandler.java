package com.ecommerce.application;

import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.event.OrderCancelEvent;
import com.ecommerce.domain.order.event.OrderCreateEvent;
import com.ecommerce.domain.order.event.OrderPayAfterEvent;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.event.StockDeductEvent;
import com.ecommerce.domain.product.event.StockRestoreEvent;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.event.PointDeductEvent;
import com.ecommerce.domain.user.event.PointRestoreEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventHandler {
    private final ObjectMapper objectMapper;
    private final OrderSecondFacade orderFacade;
    private final OrderService orderService;
    private final ProductFacade productFacade;
    private final UserFacade userFacade;
    private final ProductService productService;

    public EventHandler(ObjectMapper objectMapper, OrderSecondFacade orderFacade, OrderService orderService, ProductFacade productFacade, UserFacade userFacade, ProductService productService, PaymentUseCase paymentUseCase) {
        this.objectMapper = objectMapper;
        this.orderFacade = orderFacade;
        this.orderService = orderService;
        this.productFacade = productFacade;
        this.userFacade = userFacade;
        this.productService = productService;
    }

    @KafkaListener(topics = "order-create", groupId = "ecommerce-group")
    public void handleOrderCreateEvent(String eventJson) {
        try {
            OrderCreateEvent event = objectMapper.readValue(eventJson, OrderCreateEvent.class);
            orderFacade.createOrder(event.userId(), event.orderItems());
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "stock-deduct", groupId = "ecommerce-group")
    public void handleStockDeductEvent(String eventJson) {
        try {
            StockDeductEvent event = objectMapper.readValue(eventJson, StockDeductEvent.class);
            Product product = productService.getProduct(event.productId());
            productFacade.deductStock(product, event.quantityChange());
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "point-deduct", groupId = "ecommerce-group")
    public void handlePointDeductEvent(String eventJson) {
        try {
            PointDeductEvent event = objectMapper.readValue(eventJson, PointDeductEvent.class);
            userFacade.deductPoint(event.userId(), event.pointChange());
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "order-pay-after", groupId = "ecommerce-group")
    public void handleOrderPayAfterEvent(String eventJson) {
        try {
            OrderPayAfterEvent event = objectMapper.readValue(eventJson, OrderPayAfterEvent.class);
            orderService.saveOrder(
                    orderService.getOrder(
                            event.orderId()
                    ).finish());
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "stock-restore", groupId = "ecommerce-group")
    public void handleStockRestoreEvent(String eventJson) {
        try {
            StockRestoreEvent event = objectMapper.readValue(eventJson, StockRestoreEvent.class);
            Product product = productService.getProduct(event.productId());
            productFacade.chargeStock(product, event.quantityChange());
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "point-restore", groupId = "ecommerce-group")
    public void handlePointRestoreEvent(String eventJson) {
        try {
            PointRestoreEvent event = objectMapper.readValue(eventJson, PointRestoreEvent.class);
            userFacade.chargePoint(event.userId(), event.pointChange());
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "order-cancel", groupId = "ecommerce-group")
    public void handleOrderCancelEvent(String eventJson) {
        try {
            OrderCancelEvent event = objectMapper.readValue(eventJson, OrderCancelEvent.class);
            Long orderId = event.orderId();
            orderService.saveOrder(orderService.getOrder(orderId));
        } catch (Exception e) {
            // 예외 처리
        }
    }
}