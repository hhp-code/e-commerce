package com.ecommerce.application;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.*;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrderFacade {
    private final OrderService orderQueryService;
    private final UserService userService;
    private final ProductService productService;

    public OrderFacade(OrderService orderQueryService, UserService userService, ProductService productService) {
        this.orderQueryService = orderQueryService;
        this.userService = userService;
        this.productService = productService;
    }

    public OrderInfo.Detail getOrder(OrderQuery.GetOrder query) {
        Order order = orderQueryService.getOrder(query.orderId());
        return OrderInfo.Detail.from(order);
    }

    public List<OrderInfo.Detail> getOrders(OrderQuery.GetUserOrders query) {
        List<Order> orders = orderQueryService.getOrders(query);
        List<OrderInfo.Detail> orderDetails = new ArrayList<>();
        for (Order order : orders) {
            orderDetails.add(OrderInfo.Detail.from(order));
        }
        return orderDetails;
    }

    public OrderInfo.Summary createOrder(OrderCommand.Create command) {
        User user = userService.getUser(command.userId());
        Map<Long, Integer> items = command.items();
        Map<Product, Integer> productMap = new HashMap<>();
        items.forEach((productId, quantity) -> {
            Product product = productService.getProduct(productId);
            productMap.put(product, quantity);
        });
        Order execute = command.execute(user, productMap);
        Order order = orderQueryService.saveOrder(execute);
        return OrderInfo.Summary.from(order);
    }


}
