package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.*;
import com.ecommerce.domain.order.orderitem.OrderItemRead;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.infra.order.entity.OrderItemEntity;

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
        return new OrderEntity(
                writeModel.getUser(),
                writeModel.getItems().stream()
                        .map(OrderDomainMapper::toEntity)
                        .collect(Collectors.toList())
        );
    }

    public static OrderItemEntity toEntity(OrderItemWrite writeModel) {

        return new OrderItemEntity(
                writeModel.product(),
                writeModel.quantity()
        );
    }

    public static List<OrderRead> toReadModels(List<OrderEntity> finishedOrderWithDays) {
        return finishedOrderWithDays.stream()
                .map(OrderDomainMapper::toReadModel)
                .collect(Collectors.toList());
    }

    public static OrderWrite toWriteModel(OrderEntity orderEntity) {
        return new OrderWrite(
                orderEntity.getUser(),
                toItemWriteModel(orderEntity.getOrderItemEntities())
            );
    }

    private static List<OrderItemWrite> toItemWriteModel(List<OrderItemEntity> items) {
        List<OrderItemWrite> orderItemWrites = new ArrayList<>();
        for(OrderItemEntity item: items){
            orderItemWrites.add(new OrderItemWrite(item.getProduct(), item.getQuantity()));
        }
        return orderItemWrites;
    }
}
