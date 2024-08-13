package com.ecommerce.application;

import com.ecommerce.domain.order.OrderInfo;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderSecondFacade {
    private final OrderService orderService;
    private final UserService userService;

    public OrderSecondFacade(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    public OrderInfo.Detail getOrder(OrderQuery.GetOrder query) {
        OrderWrite orderEntity = orderService.getOrder(query.orderId());
        return OrderInfo.Detail.from(orderEntity);
    }

    public OrderInfo.Summary createOrder(long userId, List<OrderItemWrite> items) {
        OrderWrite orderWrite = new OrderWrite()
                .putUser(userService, userId)
                .addItems(items);
        OrderWrite orderEntity = orderService.saveOrder(orderWrite);
        return OrderInfo.Summary.from(orderEntity);
    }
}
