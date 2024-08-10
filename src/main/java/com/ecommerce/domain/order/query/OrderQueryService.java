package com.ecommerce.domain.order.query;

import com.ecommerce.domain.order.service.OrderDomainMapper;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.interfaces.exception.domain.OrderException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderQueryService {
    private final OrderQueryRepository orderQueryRepository;

    public OrderQueryService(OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }
    @Transactional(readOnly = true)
    public List<OrderRead> getOrders(OrderQuery.GetUserOrders query) {
        List<OrderEntity> orderEntities = orderQueryRepository.getOrders(query.userId());
        return OrderDomainMapper.toReadModels(orderEntities);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null")
    public OrderRead getOrder(Long orderId) {
        OrderEntity orderEntity = orderQueryRepository.getById(orderId)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
        return OrderDomainMapper.toReadModel(orderEntity);
    }
    @Transactional
    @Cacheable(value = "finishedOrders", key = "#durationDays", unless = "#result.isEmpty()")
    public List<OrderRead> getFinishedOrderWithDays(int durationDays) {
        List<OrderEntity> finishedOrderWithDays = orderQueryRepository.getFinishedOrderWithDays(durationDays);
        return OrderDomainMapper.toReadModels(finishedOrderWithDays);
    }


}
