package com.ecommerce.api.order.controller.dto;


import com.ecommerce.api.order.service.OrderCommand;
import com.ecommerce.api.domain.Order;

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
                order.getCartItems());
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
                order.getCartItems());

    }

    public static OrderCommand.Search toSearch(OrderDto.OrderListRequest request) {
        return new OrderCommand.Search(request.customerId());
    }

    public static OrderCommand.Add toOrderAddItem(Long orderId, OrderDto.OrderAddItemRequest request) {
        return new OrderCommand.Add(orderId,request.productId(),request.quantity());
    }

    public static OrderCommand.Payment toOrderPay(OrderDto.OrderPayRequest request) {
        return new OrderCommand.Payment(request.orderId(),request.amount());

    }
}
