package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.command.OrderCommandService;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.product.Product;
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
        OrderWrite orderEntity = orderCommandService.getOrder(command.orderId());
        OrderWrite execute = command.execute(orderEntity, productService);
        return OrderInfo.Detail.from(orderCommandService.saveOrder(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        OrderWrite queryOrderEntity = orderCommandService.getOrder(command.orderId());
        OrderWrite execute = command.execute(queryOrderEntity);
        return OrderInfo.Detail.from(orderCommandService.saveOrder(execute));
    }
}
