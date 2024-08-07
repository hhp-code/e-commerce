package com.ecommerce.api.controller.domain.order.dto;


import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.product.Product;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDto.OrderListResponse toOrderListResponse(List<Order> orders) {
        return new OrderDto.OrderListResponse(orders.stream().map(OrderMapper::convertToOrderResponse).collect(Collectors.toList()));
    }

    private static OrderDto.OrderResponse convertToOrderResponse(Order order) {
        return convertOrderResponse(order);
    }

    public static OrderCommand.Create toOrder(OrderDto.OrderCreateRequest request) {

        return new OrderCommand.Create(request.customerId(),request.items());
    }


    public static OrderDto.OrderResponse toOrderResponse(Order order) {
        return convertOrderResponse(order);

    }

    private static OrderDto.OrderResponse convertOrderResponse(Order order) {
        Map<Product, Integer> orderItems = order.getOrderItems();
        Map<Long,Integer> items = orderItems.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
        return new OrderDto.OrderResponse(order.getId(),
                order.getOrderDate(),
                order.getRegularPrice(),
                order.getSalePrice(),
                order.getSellingPrice(),
                order.getOrderStatus(),
                order.isDeleted(),
                order.getDeletedAt(),
                items);
    }

    public static OrderCommand.Search toSearch(OrderDto.OrderListRequest request) {
        return new OrderCommand.Search(request.customerId());
    }

    public static OrderCommand.Add toOrderAddItem(Long orderId, OrderDto.OrderAddItemRequest request) {
        return new OrderCommand.Add(orderId,request.productId(),request.quantity());
    }

    public static OrderCommand.Payment toOrderPay(OrderDto.OrderPayRequest request) {
        return new OrderCommand.Payment(request.orderId());
    }
    public static OrderCommand.Cancel toOrderCancel(OrderDto.OrderCancelRequest request) {
        return new OrderCommand.Cancel(request.userId());
    }

    public static OrderCommand.Delete toOrderDeleteItem(Long orderId, OrderDto.OrderDeleteItemRequest request) {
        return new OrderCommand.Delete(orderId,request.productId());
    }
}
