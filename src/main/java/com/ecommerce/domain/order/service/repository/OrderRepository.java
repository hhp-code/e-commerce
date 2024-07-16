package com.ecommerce.domain.order.service.repository;

import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> getById(Long customerId);

    List<Order> getOrders(Long customerId);

    Optional<Order> saveAndGet(Order order);

    Optional<Order> findByUserIdAndStatus(Long id, OrderStatus orderStatus);

    void deleteAll();
}
