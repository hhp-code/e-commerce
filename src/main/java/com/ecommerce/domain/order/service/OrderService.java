package com.ecommerce.domain.order.service;

import com.ecommerce.api.exception.domain.OrderException;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null")
    public Order getOrder(Long orderId) {
        return orderRepository.getById(orderId)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders(OrderCommand.Search command) {
        long id = command.orderId();
        List<Order> orders = orderRepository.getOrders(id);
        orders.size();
        return orders;
    }





    @Transactional
    @CachePut(value = "orders", key = "#result.id")
    @CacheEvict(value = "finishedOrders", allEntries = true)
    public Order saveAndGet(Order order) {
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
    }

    @Transactional
    public Order getOrderByUserId(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, OrderStatus.PREPARED)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
    }

    @Transactional
    @Cacheable(value = "finishedOrders", key = "#durationDays", unless = "#result.isEmpty()")
    public List<Order> getFinishedOrderWithDays(int durationDays) {
        return orderRepository.getFinishedOrderWithDays(durationDays);
    }


}
