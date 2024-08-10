package com.ecommerce.domain.order.query;

import com.ecommerce.infra.order.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderQueryRepository {
    Optional<OrderEntity> getById(Long customerId);

    List<OrderEntity> getOrders(Long customerId);

    List<OrderEntity> getFinishedOrderWithDays(int durationDays);
}
