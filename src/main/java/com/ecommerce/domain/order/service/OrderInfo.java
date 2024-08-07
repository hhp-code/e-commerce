package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class OrderInfo {
    public record Summary(long orderId, long userId, String status, BigDecimal totalAmount) {
        public static Summary from(Order order) {
            return new Summary(
                    order.getId(),
                    order.getUser().getId(),
                    order.getOrderStatus(),
                    order.getTotalAmount()
            );
        }
    }
    public record Detail(long orderId, long userId, LocalDateTime orderDate, String status, BigDecimal totalAmount,
                         List<OrderItemInfo> items) {
        public static Detail from(Order order) {
            return new Detail(
                    order.getId(),
                    order.getUser().getId(),
                    order.getOrderDate(),
                    order.getOrderStatus(),
                    order.getTotalAmount(),
                    order.getOrderItems().entrySet().stream()
                            .map(entry -> new OrderItemInfo(entry.getKey().getId(), entry.getValue()))
                            .collect(Collectors.toList())
            );
        }
    }
    public record OrderItemInfo(long productId, int quantity) {}
}
