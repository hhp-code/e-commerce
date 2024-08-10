package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.service.OrderDomainMapper;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.infra.order.entity.OrderEntity;
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
        OrderEntity orderEntity = orderService.getOrder(command.orderId());
        OrderWrite afterExecute = command.execute(OrderDomainMapper.toWriteModel(orderEntity), productService);
        OrderWrite execute = command.execute(afterExecute, productService);
        return OrderInfo.Detail.from(orderService.saveOrder(execute));
    }

    public OrderInfo.Detail deleteItemFromOrder(OrderCommand.Delete command) {
        OrderEntity queryOrderEntity = orderService.getOrder(command.orderId());
        OrderWrite execute = command.execute(OrderDomainMapper.toWriteModel(queryOrderEntity));
        return OrderInfo.Detail.from(orderService.saveOrder(execute));
    }
}
