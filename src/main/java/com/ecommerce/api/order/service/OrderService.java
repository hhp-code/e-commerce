package com.ecommerce.api.order.service;

import com.ecommerce.api.domain.*;
import com.ecommerce.api.order.service.repository.OrderRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.product.service.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long customerId) {
        return orderRepository.getById(customerId).orElseThrow(
                () -> new RuntimeException("주문이 존재하지 않습니다.")
        );
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders(OrderCommand.Search search){
        return orderRepository.getOrders(search);
    }

    @Transactional
    public Order createOrder(OrderCommand.Create command) {
        User user = userRepository.getById(command.id()).orElseThrow(
                ()-> new RuntimeException("사용자가 존재하지 않습니다.")
        );
        Order order = new Order(user,command.items());
        order.start();
        return orderRepository.saveAndGet(order).orElseThrow(
                () -> new RuntimeException("주문 생성에 실패하였습니다.")
        );
    }
    @Transactional
    public Order addCartItemToOrder(OrderCommand.Add command) {
        User user = userRepository.getById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + command.userId()));

        Order order = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PREPARED)
                .orElseGet(() -> {
                    Order newOrder = new Order(user, new ArrayList<>());
                    newOrder.start(); // 주문 상태를 PREPARED로 설정
                    return newOrder;
                });

        Product product = productRepository.getProduct(command.productId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + command.productId()));

        if (product.getAvailableStock() < command.quantity()) {
            throw new IllegalStateException("상품의 재고가 부족합니다. 상품 ID: " + command.productId());
        }

        CartItem cartItem = new CartItem(product, command.quantity());
        order.addCartItem(cartItem);

        return orderRepository.saveAndGet(order)
                .orElseThrow(() -> new RuntimeException("주문 저장에 실패했습니다."));
    }

    @Transactional
    public Order payOrder(OrderCommand.Payment orderPay) {
        if(orderPay == null){
            throw new RuntimeException("결제 정보가 없습니다.");
        }
        User user = userRepository.getById(orderPay.orderId())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        Order order = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PREPARED)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));

        List<CartItem> cartItems = order.getCartItems();

        for (CartItem item : cartItems) {
            Product product = productRepository.getProduct(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

            int orderedQuantity = item.getQuantity();
            int updatedRows = productRepository.decreaseStock(product.getId(), orderedQuantity);
            if (updatedRows == 0) {
                throw new RuntimeException("상품 " + product.getName() + "의 재고가 부족합니다.");
            }
        }

        order.finish();
        // 데이터 플랫폼으로 아무튼 보냄
        return orderRepository.saveAndGet(order).orElseThrow(()-> new RuntimeException("주문 완료에 실패했습니다."));
    }

}
