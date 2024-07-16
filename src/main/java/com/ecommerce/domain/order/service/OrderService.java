package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.getById(orderId)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders(OrderCommand.Search search) {
        long id = search.id();
        return orderRepository.getOrders(id);
    }

    @Transactional
    public Order createOrder(OrderCommand.Create command) {
        User user = userService.getUser(command.id());
        Order order = new Order(user, command.items());
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new RuntimeException("주문 생성에 실패하였습니다."));
    }
    @Transactional
    public Order saveAndGet(Order order) {
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new RuntimeException("주문 저장에 실패하였습니다."));
    }

    @Transactional
    public Order getOrCreateOrder(OrderCommand.Add command) {
        return orderRepository.findByUserIdAndStatus(command.userId(), OrderStatus.PREPARED )
                .orElseGet(() -> createOrder(new OrderCommand.Create(command.userId(), List.of())));
    }

    @Transactional
    public Order getOrderByUserId(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, OrderStatus.PREPARED)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
    }

}
