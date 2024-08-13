package com.ecommerce.domain.order;

import com.ecommerce.domain.order.command.OrderCommandRepository;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.query.OrderQueryRepository;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.interfaces.exception.domain.OrderException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderService {
    private final OrderQueryRepository orderQueryRepository;
    private final OrderCommandRepository orderCommandRepository;
    public OrderService(OrderQueryRepository orderQueryRepository, OrderCommandRepository orderCommandRepository) {
        this.orderQueryRepository = orderQueryRepository;
        this.orderCommandRepository = orderCommandRepository;
    }
    @Transactional(readOnly = true)
    public List<OrderRead> getOrders(OrderQuery.GetUserOrders query) {
        List<OrderEntity> orderEntities = orderQueryRepository.getOrders(query.userId());
        return OrderDomainMapper.toReadModels(orderEntities);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null")
    public OrderWrite getOrder(Long orderId) {
        OrderEntity orderEntity = orderQueryRepository.getById(orderId)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
        return OrderDomainMapper.toWriteModel(orderEntity);
    }
    @Transactional
    @Cacheable(value = "finishedOrders", key = "#durationDays", unless = "#result.isEmpty()")
    public List<OrderRead> getFinishedOrderWithDays(int durationDays) {
        List<OrderEntity> finishedOrderWithDays = orderQueryRepository.getFinishedOrderWithDays(durationDays);
        return OrderDomainMapper.toReadModels(finishedOrderWithDays);
    }
    @Transactional
    @Caching(
            put = {@CachePut(value = "orders", key = "#result.id")},
            evict = {@CacheEvict(value = "finishedOrders", allEntries = true)}
    )
    public OrderWrite saveOrder(OrderWrite orderWrite) {
        OrderEntity entity = OrderDomainMapper.toEntity(orderWrite);
        OrderEntity orderEntity = orderCommandRepository.saveAndGet(entity)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
        return OrderDomainMapper.toWriteModel(orderEntity);
    }



}
