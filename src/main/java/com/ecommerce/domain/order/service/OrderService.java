package com.ecommerce.domain.order.service;

import com.ecommerce.api.exception.domain.OrderException;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.user.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, UserService userService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.productService = productService;
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
    public Order createOrder(OrderCommand.Create command) {
        User user = userService.getUser(command.userId());
        Map<Product, Integer> convertedOrders = getProductIntegerMap(command);
        Order order = new Order(user, convertedOrders);
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 생성에 실패하였습니다."));
    }

    private Map<Product, Integer> getProductIntegerMap(OrderCommand.Create command) {
        Map<Long, Integer> items = command.items();
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity <= 0) {
                throw new OrderException.ServiceException("주문 수량은 0보다 커야 합니다. 상품 ID: " + productId);
            }

            Product product = productService.getProduct(productId);
            if (product.getStock() < quantity) {
                throw new OrderException.ServiceException("상품의 재고가 부족합니다. 상품 ID: " + productId);
            }
        }

        return command.items().entrySet().stream()
                .collect(Collectors.toMap(entry -> productService.getProduct(entry.getKey()), Map.Entry::getValue));
    }

    @Transactional
    public Order saveAndGet(Order order) {
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 저장에 실패하였습니다."));
    }

    @Transactional
    public Order getOrCreateOrder(OrderCommand.Add command) {
        return orderRepository.findByUserIdAndStatus(command.userId(), OrderStatus.PREPARED )
                .orElseGet(() -> createOrder(new OrderCommand.Create(command.userId(), Map.of())));
    }

    @Transactional
    public Order getOrderByUserId(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, OrderStatus.PREPARED)
                .orElseThrow(() -> new OrderException.ServiceException("주문이 존재하지 않습니다."));
    }

    @Transactional
    public Order cancelOrder(OrderCommand.Cancel request) {
        Order order = getOrderByUserId(request.userId());
        order.cancel();
        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new OrderException.ServiceException("주문 취소에 실패하였습니다."));
    }

    @Transactional
    public List<Order> getFinishedOrderWithDays(int durationDays) {
        return orderRepository.getFinishedOrderWithDays(durationDays);
    }


    public List<Order> createOrdersBatch(List<OrderCommand.Create> orderBatch) {
        return orderBatch.stream()
                .map(this::createOrder)
                .collect(Collectors.toList());
    }
}
