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
    private final ProductService productService;

    public CartUseCase(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    public OrderInfo.Detail addItemToOrder(OrderCommand.Add command) {
        Order execute = command.execute(orderService, productService);
        return OrderInfo.Detail.from(orderService.saveAndGet(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        Order execute = command.execute(orderService, productService);
        return OrderInfo.Detail.from(orderService.saveAndGet(execute));
    }
}
