package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class CartUseCase {
    private final OrderService orderQueryService;
    private final ProductService productService;

    public CartUseCase(OrderService orderQueryService, ProductService productService) {
        this.orderQueryService = orderQueryService;
        this.productService = productService;
    }

    public OrderInfo.Detail addItemToOrder(OrderCommand.Add command) {
        Order queryOrder = orderQueryService.getOrder(command.orderId());
        Order execute = command.execute(queryOrder, productService);
        return OrderInfo.Detail.from(orderQueryService.saveOrder(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        Order queryOrder = orderQueryService.getOrder(command.orderId());
        Order execute = command.execute(queryOrder, productService);
        return OrderInfo.Detail.from(orderQueryService.saveOrder(execute));
    }
}
