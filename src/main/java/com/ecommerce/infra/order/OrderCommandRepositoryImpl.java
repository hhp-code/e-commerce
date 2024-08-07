package com.ecommerce.infra.order;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderCommandRepository;

import java.util.Optional;

public class OrderCommandRepositoryImpl implements OrderCommandRepository {
    private final OrderJPARepository orderJPARepository;

    public OrderCommandRepositoryImpl(OrderJPARepository orderJPARepository) {
        this.orderJPARepository = orderJPARepository;

    }

    @Override
    public Optional<Order> saveAndGet(Order order) {
        return Optional.of(orderJPARepository.save(order));
    }
}
