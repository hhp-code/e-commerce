package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class CartUseCase {
    private final OrderService orderService;
    private final OrderService orderService;
    private final ProductService productService;

    public CartUseCase(OrderService orderService, OrderService orderQueryService, ProductService productService) {
        this.orderService = orderService;
        this.orderService = orderQueryService;
        this.productService = productService;
    }

    public OrderInfo.Detail addItemToOrder(OrderCommand.Add command) {
        Order queryOrder = orderService.getOrder(command.orderId());
        Order execute = command.execute(queryOrder, productService);
        return OrderInfo.Detail.from(orderService.saveOrder(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        Order queryOrder = orderService.getOrder(command.orderId());
        Order execute = command.execute(queryOrder, productService);
        return OrderInfo.Detail.from(orderService.saveOrder(execute));
    }
}
