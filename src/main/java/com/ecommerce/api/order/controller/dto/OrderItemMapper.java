package com.ecommerce.api.order.controller.dto;

import com.ecommerce.api.order.service.OrderCommand;
import com.ecommerce.domain.OrderItem;
import com.ecommerce.domain.Product;

public class OrderItemMapper {
    public static OrderItemDto.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemDto.OrderItemResponse(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }

    public static OrderCommand.OrderItemCommand toOrderItemCommand(OrderItemDto.OrderItemCreateRequest request) {
        return new OrderCommand.OrderItemCommand(
                request.productId(),
                request.quantity()
        );
    }

    public static OrderItem toOrderItem(OrderCommand.OrderItemCommand command) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(new Product());
        orderItem.setQuantity(command.quantity());
        return orderItem;
    }
}
