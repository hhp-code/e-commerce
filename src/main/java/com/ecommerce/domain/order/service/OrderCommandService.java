package com.ecommerce.domain.order.service;

import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCommandService {
    private final OrderRepository orderRepository;

    public OrderCommandService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    @CachePut(value = "orders", key = "#result.id")
    @CacheEvict(value = "finishedOrders", allEntries = true)
    public Order saveAndGet(Order order) {
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
    }


}
