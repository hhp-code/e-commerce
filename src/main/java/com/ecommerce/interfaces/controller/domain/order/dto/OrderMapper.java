package com.ecommerce.interfaces.controller.domain.order.dto;


import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.query.OrderQuery;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDto.OrderListResponse toOrderListResponse(List<OrderInfo.Detail> info) {
        return new OrderDto.OrderListResponse(info.stream().map(OrderMapper::toOrderDetailResponse).collect(Collectors.toList()));
    }


    public static OrderCommand.Create toOrder(OrderDto.OrderCreateRequest request) {
        return new OrderCommand.Create(request.customerId(),request.items());
    }


    public static OrderDto.OrderDetailResponse toOrderDetailResponse(OrderInfo.Detail info) {
        return new OrderDto.OrderDetailResponse(info.orderId(),
                        info.userId(), info.status(), info.totalAmount(), info.orderDate(),
                info.items().stream().collect(Collectors.toMap(OrderInfo.OrderItemInfo::productId, OrderInfo.OrderItemInfo::quantity)));
    }
    public static OrderDto.OrderSummaryResponse toOrderSummaryResponse(OrderInfo.Summary info) {
        return new OrderDto.OrderSummaryResponse(info.orderId(),info.userId(),info.status(),info.totalAmount());
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

    public static OrderQuery.GetUserOrders toGetUserOrders(OrderDto.OrderListRequest request) {
        return new OrderQuery.GetUserOrders(request.customerId());
    }

    public static OrderQuery.GetOrder toGetOrder(Long orderId) {
        return new OrderQuery.GetOrder(orderId);
    }
}
