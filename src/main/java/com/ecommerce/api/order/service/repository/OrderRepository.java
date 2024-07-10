package com.ecommerce.api.order.service.repository;

import com.ecommerce.api.order.service.OrderCommand;
import com.ecommerce.api.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> getById(Long customerId);

    List<Order> getOrders(OrderCommand.Search search);

    Optional<Order> saveAndGet(Order order);
}
