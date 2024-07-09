package com.ecommerce.api.order.controller.dto;


import com.ecommerce.api.order.service.OrderCommand;
import com.ecommerce.domain.Order;
import com.ecommerce.domain.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDto.OrderListResponse toOrderListResponse(List<Order> orders) {
        return new OrderDto.OrderListResponse(orders.stream().map(OrderMapper::convertToOrderResponse).collect(Collectors.toList()));
    }

    private static OrderDto.OrderResponse convertToOrderResponse(Order order) {
        return new OrderDto.OrderResponse(order.getId(),
                order.getOrderDate(),
                order.getRegularPrice(),
                order.getSalePrice(),
                order.getSellingPrice(),
                order.getStatus(),
                order.getIsDeleted(),
                order.getDeletedAt(),
                order.getOrderItems());
    }

    public static OrderCommand.Create toOrder(OrderDto.OrderCreateRequest request) {
        return new OrderCommand.Create(request.customerId(),request.items());
    }


    public static OrderDto.OrderResponse toOrderResponse(Order order) {
        return new OrderDto.OrderResponse(order.getId(),
                order.getOrderDate(),
                order.getRegularPrice(),
                order.getSalePrice(),
                order.getSellingPrice(),
                order.getStatus(),
                order.getIsDeleted(),
                order.getDeletedAt(),
                order.getOrderItems());

    }

    public static OrderCommand.Search toSearch(OrderDto.OrderListRequest request) {
        return new OrderCommand.Search(request.customerId());
    }
}
