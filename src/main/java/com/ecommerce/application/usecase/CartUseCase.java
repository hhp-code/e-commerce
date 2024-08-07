package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderCommandService;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.product.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class CartUseCase {
    private final OrderCommandService orderCommandService;
    private final ProductService productService;

    public CartUseCase(OrderCommandService orderCommandService, ProductService productService) {
        this.orderCommandService = orderCommandService;
        this.productService = productService;
    }

    public OrderInfo.Detail addItemToOrder(OrderCommand.Add command) {
        Order execute = command.execute(orderCommandService, productService);
        return OrderInfo.Detail.from(orderCommandService.saveOrder(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        Order execute = command.execute(orderCommandService, productService);
        return OrderInfo.Detail.from(orderCommandService.saveOrder(execute));
    }
}
