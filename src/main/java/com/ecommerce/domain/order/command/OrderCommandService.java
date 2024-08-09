package com.ecommerce.domain.order.command;

import com.ecommerce.domain.order.service.OrderDomainMapper;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.query.OrderQueryRepository;
import com.ecommerce.interfaces.exception.domain.OrderException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCommandService {
    private final OrderCommandRepository orderCommandRepository;
    private final OrderQueryRepository orderQueryRepository;
    public OrderCommandService(OrderCommandRepository orderCommandRepository, OrderQueryRepository orderQueryRepository) {
        this.orderCommandRepository = orderCommandRepository;
        this.orderQueryRepository = orderQueryRepository;
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

    @Transactional(readOnly = true)
    public OrderWrite getOrder(Long orderId) {
        OrderEntity orderEntity = orderQueryRepository.getById(orderId)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
        return OrderDomainMapper.toWriteModel(orderEntity);
    }

}
