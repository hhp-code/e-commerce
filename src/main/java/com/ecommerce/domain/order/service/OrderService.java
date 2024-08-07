package com.ecommerce.domain.order.service;

import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<OrderInfo.Detail> getOrders(OrderQuery.GetUserOrders query) {
        List<Order> orders = orderRepository.getOrders(query.userId());
        orders.size();
        List<OrderInfo.Detail> orderDetails = new ArrayList<>();
        for (Order order : orders) {
            orderDetails.add(OrderInfo.Detail.from(order));
        }
        return orderDetails;
    }


    @Transactional
    @CachePut(value = "orders", key = "#result.id")
    @CacheEvict(value = "finishedOrders", allEntries = true)
    public Order saveAndGet(Order order) {
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
    }


    @Transactional
    @Cacheable(value = "finishedOrders", key = "#durationDays", unless = "#result.isEmpty()")
    public List<Order> getFinishedOrderWithDays(int durationDays) {
        return orderRepository.getFinishedOrderWithDays(durationDays);
    }


}
