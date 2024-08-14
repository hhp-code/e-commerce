package com.ecommerce.application;

import com.ecommerce.domain.order.OrderInfo;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.domain.order.OrderWrite;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFacade {
    private final OrderService orderService;

    public OrderFacade(OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderInfo.Detail getOrder(OrderQuery.GetOrder query) {
        OrderWrite orderEntity = orderService.getOrder(query.orderId());
        return OrderInfo.Detail.from(orderEntity);
    }

    public List<OrderInfo.Detail> getOrders(OrderQuery.GetUserOrders query) {
        List<OrderRead> orderEntities = orderService.getOrders(query);
        List<OrderInfo.Detail> orderDetails = new ArrayList<>();
        for (OrderRead orderEntity : orderEntities) {
            orderDetails.add(OrderInfo.Detail.from(orderEntity));
        }
        return orderDetails;
    }

}
