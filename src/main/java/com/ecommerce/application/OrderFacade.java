package com.ecommerce.application;

import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.command.OrderCommandService;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.query.OrderQueryService;
import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.service.*;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFacade {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;
    private final UserService userService;

    public OrderFacade(OrderQueryService orderQueryService, OrderCommandService orderCommandService, UserService userService) {
        this.orderQueryService = orderQueryService;
        this.orderCommandService = orderCommandService;
        this.userService = userService;
    }
    public OrderInfo.Detail getOrder(OrderQuery.GetOrder query){
        OrderRead orderEntity = orderQueryService.getOrder(query.orderId());
        return OrderInfo.Detail.from(orderEntity);
    }
    public List<OrderInfo.Detail> getOrders(OrderQuery.GetUserOrders query){
        List<OrderRead> orderEntities = orderQueryService.getOrders(query);
        List<OrderInfo.Detail> orderDetails = new ArrayList<>();
        for (OrderRead orderEntity : orderEntities) {
            orderDetails.add(OrderInfo.Detail.from(orderEntity));
        }
        return orderDetails;
    }
    public OrderInfo.Summary createOrder(OrderCommand.Create command) {
        OrderWrite execute = command.execute(userService);
        OrderWrite orderEntity = orderCommandService.saveOrder(execute);
        return OrderInfo.Summary.from(orderEntity);
    }
}
