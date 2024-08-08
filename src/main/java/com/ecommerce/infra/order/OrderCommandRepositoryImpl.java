package com.ecommerce.infra.order;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderCommandRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
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
