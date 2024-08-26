package com.ecommerce.domain.order;

import com.ecommerce.domain.order.orderitem.OrderItemRead;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.product.ProductDomainMapper;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.UserDomainMapper;
import com.ecommerce.domain.user.User;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.infra.order.entity.OrderItemEntity;
import com.ecommerce.infra.product.entity.ProductEntity;
import com.ecommerce.infra.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDomainMapper {

    public static OrderRead toReadModel(OrderEntity orderEntity) {
        return new OrderRead(
                orderEntity.getId(),
                orderEntity.getUser().getId(),
                orderEntity.getOrderStatus(),
                orderEntity.getTotalAmount(),
                orderEntity.getOrderItemEntities().stream()
                        .map(OrderDomainMapper::toReadModel)
                        .collect(Collectors.toList()),
                orderEntity.getOrderDate()
        );
    }

    public static OrderItemRead toReadModel(OrderItemEntity item) {
        return new OrderItemRead(
                item.getProduct().getId(),
                item.getQuantity()
        );
    }

    public static OrderEntity toEntity(OrderWrite writeModel) {
        User user = writeModel.getUser();
        UserEntity entity = UserDomainMapper.toEntity(user);
        return new OrderEntity(
                entity,
                writeModel.getItems().stream()
                        .map(OrderDomainMapper::toEntity)
                        .collect(Collectors.toList())
        );
    }

    public static OrderItemEntity toEntity(OrderItemWrite writeModel) {
        Product product = writeModel.product();
        ProductEntity entity = ProductDomainMapper.toEntity(product);

        return new OrderItemEntity(
                entity,
                writeModel.quantity()
        );
    }

    public static List<OrderRead> toReadModels(List<OrderEntity> finishedOrderWithDays) {
        return finishedOrderWithDays.stream()
                .map(OrderDomainMapper::toReadModel)
                .collect(Collectors.toList());
    }

    public static OrderWrite toWriteModel(OrderEntity orderEntity) {
        UserEntity user = orderEntity.getUser();
        User userWrite = UserDomainMapper.toWriteModel(user);
        return new OrderWrite(
                userWrite,
                toItemWriteModel(orderEntity.getOrderItemEntities())
            );
    }

    private static List<OrderItemWrite> toItemWriteModel(List<OrderItemEntity> items) {
        List<OrderItemWrite> orderItemWrites = new ArrayList<>();
        for(OrderItemEntity item: items){
            ProductEntity product = item.getProduct();
            Product writeModel = ProductDomainMapper.toWriteModel(product);
            orderItemWrites.add(new OrderItemWrite(writeModel, item.getQuantity()));
        }
        return orderItemWrites;
    }
}
