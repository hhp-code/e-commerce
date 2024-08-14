package com.ecommerce.application;

import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.event.*;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.product.ProductWrite;
import com.ecommerce.domain.product.event.StockDeductEvent;
import com.ecommerce.domain.product.event.StockChargeEvent;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.UserWrite;
import com.ecommerce.domain.user.event.PointDeductEvent;
import com.ecommerce.domain.user.event.PointChargeEvent;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventHandler {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;

    public EventHandler(ObjectMapper objectMapper, OrderService orderService, ProductService productService, UserService userService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
    }

    @KafkaListener(topics = "order-create", groupId = "ecommerce-group")
    public void handleOrderCreateEvent(String eventJson) {
        try {
            OrderCreateEvent event = objectMapper.readValue(eventJson, OrderCreateEvent.class);
            UserWrite user = userService.getUser(event.userId());
            OrderWrite orderWrite = new OrderWrite(user).addItems(event.orderItems());
            orderService.saveOrder(orderWrite);
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "stock-deduct", groupId = "ecommerce-group")
    public void handleStockDeductEvent(String eventJson) {
        try {
            StockDeductEvent event = objectMapper.readValue(eventJson, StockDeductEvent.class);
            ProductWrite product = productService.getProduct(event.productId()).deductStock(event.quantityChange());
            productService.saveAndGet(product);
        } catch (Exception e) {
            // 예외 처리
        }
    }
    @KafkaListener(topics= "order-item-add", groupId = "ecommerce-group")
    public void handleOrderAddItemEvent(String eventJson) {
        try {
            OrderItemAddEvent event = objectMapper.readValue(eventJson, OrderItemAddEvent.class);
            ProductWrite product = productService.getProduct(event.productId());
            int quantity = event.quantity();
            OrderItemWrite item = new OrderItemWrite(product, quantity);
            orderService.saveOrder(
                    orderService.getOrder(
                            event.orderId()
                    ).addItem(
                            item
                    )
            );
        } catch (Exception e) {
            // 예외 처리
        }
    }
    @KafkaListener(topics = "order-item-delete", groupId = "ecommerce-group")
    public void handleOrderDeleteItemEvent(String eventJson) {
        try {
            OrderItemDeleteEvent event = objectMapper.readValue(eventJson, OrderItemDeleteEvent.class);

            orderService.saveOrder(
                    orderService.getOrder(
                            event.orderId()
                    ).deleteItem(
                            event.productId()
                    )
            );
        } catch (Exception e) {
            // 예외 처리
        }
    }




    @KafkaListener(topics = "point-deduct", groupId = "ecommerce-group")
    public void handlePointDeductEvent(String eventJson) {
        try {
            PointDeductEvent event = objectMapper.readValue(eventJson, PointDeductEvent.class);
            UserWrite userWrite = userService.getUser(event.userId()).deductPoint(event.pointChange());
            userService.saveUser(userWrite);
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
            StockChargeEvent event = objectMapper.readValue(eventJson, StockChargeEvent.class);
            ProductWrite product = productService.getProduct(event.productId()).chargeStock(event.quantityChange());
            productService.saveAndGet(product);
        } catch (Exception e) {
            // 예외 처리
        }
    }

    @KafkaListener(topics = "point-restore", groupId = "ecommerce-group")
    public void handlePointRestoreEvent(String eventJson) {
        try {
            PointChargeEvent event = objectMapper.readValue(eventJson, PointChargeEvent.class);
            UserWrite userWrite = userService.getUser(event.userId()).chargePoint(event.pointChange());
            userService.saveUser(userWrite);
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