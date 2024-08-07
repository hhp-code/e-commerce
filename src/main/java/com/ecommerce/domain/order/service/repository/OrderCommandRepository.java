package com.ecommerce.domain.order.service.repository;

import com.ecommerce.domain.order.Order;

import java.util.Optional;

public interface OrderCommandRepository {
    Optional<Order> saveAndGet(Order order);

}
