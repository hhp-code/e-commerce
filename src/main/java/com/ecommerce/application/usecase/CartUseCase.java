package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.OrderInfo;
import com.ecommerce.domain.product.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class CartUseCase {
    private final OrderService orderService;
    private final ProductService productService;

    public CartUseCase( OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    public OrderInfo.Detail addItemToOrder(OrderCommand.Add command) {
        OrderWrite orderEntity = orderService.getOrder(command.orderId());
        OrderWrite execute = command.execute(orderEntity, productService);
        return OrderInfo.Detail.from(orderService.saveOrder(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        OrderWrite queryOrderEntity = orderService.getOrder(command.orderId());
        OrderWrite execute = command.execute(queryOrderEntity);
        return OrderInfo.Detail.from(orderService.saveOrder(execute));
    }
}
