package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderCommandRepository;
import com.ecommerce.domain.order.service.repository.OrderQueryRepository;
import com.ecommerce.interfaces.exception.domain.OrderException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
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

    //    @Transactional
//    @Caching(
//            put = {@CachePut(value = "orders", key = "#result.id")},
//            evict = {@CacheEvict(value = "finishedOrders", allEntries = true)}
//    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = {"orders", "finishedOrders"}, allEntries = true)
    public Order saveOrder(Order order) {
        return orderCommandRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
    }


}
