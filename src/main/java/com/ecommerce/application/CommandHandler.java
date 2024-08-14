package com.ecommerce.application;

import com.ecommerce.domain.event.DomainEvent;
import com.ecommerce.domain.event.EventBus;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.event.*;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.product.event.StockDeductEvent;
import com.ecommerce.domain.product.event.StockChargeEvent;
import com.ecommerce.domain.user.command.UserCommand;
import com.ecommerce.domain.user.event.PointDeductEvent;
import com.ecommerce.domain.user.event.PointChargeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class CommandHandler {
    private final EventBus eventBus;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;


    public CommandHandler(EventBus eventBus, ObjectMapper objectMapper, OrderService orderService) {
        this.eventBus = eventBus;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    public void handle(Object command) {
        List<DomainEvent> events = switch (command) {
            case OrderCommand.Create c -> handleCreateOrder(c);
            case OrderCommand.Payment p -> handlePayment(p);
            case OrderCommand.Cancel c -> handleCancelOrder(c);
            case OrderCommand.Add a-> handleAddItemToOrder(a);
            case OrderCommand.Delete d -> handleDeleteItemFromOrder(d);
            case UserCommand.Charge c-> handleChargePoint(c);
            case null, default -> {
                assert command != null;
                throw new IllegalArgumentException("Unknown command type: " + command.getClass().getSimpleName());
            }
        };

        publishEvents(events);
    }

    private List<DomainEvent> handleChargePoint(UserCommand.Charge c) {
        long userId = c.userId();
        BigDecimal amount = c.amount();
        return List.of(
                new PointChargeEvent(userId, amount)
        );
    }

    private List<DomainEvent> handleDeleteItemFromOrder(OrderCommand.Delete d) {
        long orderId = d.orderId();
        long productId = d.productId();
        return List.of(
                new OrderItemDeleteEvent(orderId, productId)
        );
    }

    private List<DomainEvent> handleAddItemToOrder(OrderCommand.Add a) {
        long orderId = a.orderId();
        long productId = a.productId();
        int quantity = a.quantity();
        return List.of(
                new OrderItemAddEvent(orderId, productId,quantity)
        );
    }

    private List<DomainEvent> handleCreateOrder(OrderCommand.Create command) {
        long userId = command.userId();
        List<OrderItemWrite> items = command.items();
        return List.of(
                new OrderCreateEvent(userId, items)
        );
    }

    private List<DomainEvent> handlePayment(OrderCommand.Payment command) {
        OrderWrite order = orderService.getOrder(command.orderId());
        List<OrderItemWrite> items = order.getItems();
        List<DomainEvent> events = new ArrayList<>();
        for(OrderItemWrite item : items) {
            events.add(new StockDeductEvent(item.product().getId(), item.quantity()));
        }
        events.add(new PointDeductEvent(order.getUserId(), order.getTotalAmount()));
        events.add(new OrderPayAfterEvent(order.getId()));
        return events;
    }

    private List<DomainEvent> handleCancelOrder(OrderCommand.Cancel command) {
        OrderWrite order = orderService.getOrder(command.orderId());
        List<DomainEvent> events = new ArrayList<>();
        for(OrderItemWrite item : order.getItems()) {
            events.add(new StockChargeEvent(item.product().getId(), item.quantity()));
        }
        events.add(new PointChargeEvent(order.getUserId(), order.getTotalAmount()));
        events.add(new OrderCancelEvent(order.getId()));
        return events;
    }

    private void publishEvents(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            try {
                String topic = event.getEventType();
                String payload = objectMapper.writeValueAsString(event);
                eventBus.publish(topic, payload);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize event", e);
            }
        }
    }

}