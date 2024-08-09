package com.ecommerce.infra.order;

import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.order.command.OrderCommandRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public class OrderCommandRepositoryImpl implements OrderCommandRepository {
    private final OrderJPARepository orderJPARepository;

    public OrderCommandRepositoryImpl(OrderJPARepository orderJPARepository) {
        this.orderJPARepository = orderJPARepository;

    }

    @Override
    public Optional<OrderEntity> saveAndGet(OrderEntity orderEntity) {
        return Optional.of(orderJPARepository.save(orderEntity));
    }
}
