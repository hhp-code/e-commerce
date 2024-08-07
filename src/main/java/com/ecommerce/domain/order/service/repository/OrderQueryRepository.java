package com.ecommerce.domain.order.service.repository;

import com.ecommerce.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderQueryRepository {
    Optional<Order> getById(Long customerId);

    List<Order> getOrders(Long customerId);

    List<Order> getFinishedOrderWithDays(int durationDays);
}
