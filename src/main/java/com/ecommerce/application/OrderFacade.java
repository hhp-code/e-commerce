package com.ecommerce.application;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderQuery;
import com.ecommerce.domain.order.service.OrderQueryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFacade {
    private final OrderQueryService orderQueryService;

    public OrderFacade(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }
    public OrderInfo.Detail getOrder(OrderQuery.GetOrder query){
        Order order = orderQueryService.getOrder(query.orderId());
        return OrderInfo.Detail.from(order);
    }
    public List<OrderInfo.Detail> getOrders(OrderQuery.GetUserOrders query){
        List<Order> orders = orderQueryService.getOrders(query);
        List<OrderInfo.Detail> orderDetails = new ArrayList<>();
        for (Order order : orders) {
            orderDetails.add(OrderInfo.Detail.from(order));
        }
        return orderDetails;
    }
}
