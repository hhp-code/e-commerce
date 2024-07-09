package com.ecommerce.api.order.repository;

import com.ecommerce.api.order.service.OrderCommand;
import com.ecommerce.api.order.service.repository.OrderRepository;
import com.ecommerce.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJPARepository orderJPARepository;

    public OrderRepositoryImpl(OrderJPARepository orderJPARepository) {
        this.orderJPARepository = orderJPARepository;
    }

    @Override
    public Optional<Order> getById(Long customerId) {
        return orderJPARepository.findById(customerId);
    }

    @Override
    public List<Order> getOrders(OrderCommand.Search search) {
        return orderJPARepository.findById(search.id()).map(List::of).orElse(List.of());
    }

    @Override
    public Optional<Order> saveAndGet(Order order) {
        return Optional.of(orderJPARepository.save(order));
    }
}
