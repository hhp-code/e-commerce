package com.ecommerce.application;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderQuery;
import com.ecommerce.domain.order.service.OrderService;
import org.springframework.stereotype.Component;

@Component
public class OrderFacade {
    private final OrderService orderService;

    public OrderFacade(OrderService orderService) {
        this.orderService = orderService;
    }
    public OrderInfo.Detail getOrder(OrderQuery.GetOrder query){
        Order order = orderService.getOrder(query.orderId());
        return OrderInfo.Detail.from(order);
    }
}
