package com.ecommerce.domain.order.command;

import com.ecommerce.infra.order.entity.OrderEntity;

import java.util.Optional;

public interface OrderCommandRepository {
    Optional<OrderEntity> saveAndGet(OrderEntity orderEntity);

}
