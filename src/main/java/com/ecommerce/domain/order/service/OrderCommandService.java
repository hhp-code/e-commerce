package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.service.repository.OrderCommandRepository;
import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.domain.order.Order;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCommandService {
    private final OrderCommandRepository orderCommandRepository;

    public OrderCommandService(OrderCommandRepository orderCommandRepository) {
        this.orderCommandRepository = orderCommandRepository;
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "orders", key = "#result.id")},
            evict = {@CacheEvict(value = "finishedOrders", allEntries = true)}
    )
    public Order saveOrder(Order order) {
        return orderCommandRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
    }



}
