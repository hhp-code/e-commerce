package com.ecommerce.application;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.*;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderFacade {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;
    private final UserService userService;
    private final ProductService productService;

    public OrderFacade(OrderQueryService orderQueryService, OrderCommandService orderCommandService, UserService userService, ProductService productService) {
        this.orderQueryService = orderQueryService;
        this.orderCommandService = orderCommandService;
        this.userService = userService;
        this.productService = productService;
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
    public OrderInfo.Summary createOrder(OrderCommand.Create command) {
        Order execute = command.execute(userService, productService);
        Order order = orderCommandService.saveOrder(execute);
        return OrderInfo.Summary.from(order);
    }
}
