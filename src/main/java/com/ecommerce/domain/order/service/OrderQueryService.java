package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderQueryRepository;
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
    public List<Order> getOrders(OrderQuery.GetUserOrders query) {
        List<Order> orders = orderQueryRepository.getOrders(query.userId());
        orders.size();
        return orders;
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null")
    public Order getOrder(Long orderId) {
        return orderQueryRepository.getById(orderId)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
    }
    @Transactional
    @Cacheable(value = "finishedOrders", key = "#durationDays", unless = "#result.isEmpty()")
    public List<Order> getFinishedOrderWithDays(int durationDays) {
        return orderQueryRepository.getFinishedOrderWithDays(durationDays);
    }


}
