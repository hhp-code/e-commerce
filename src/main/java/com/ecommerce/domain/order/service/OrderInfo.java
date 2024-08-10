package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.domain.order.OrderWrite;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class OrderInfo {
    public record Summary(long orderId, long userId, String status, BigDecimal totalAmount) {
        public static Summary from(OrderRead orderEntity) {
            return new Summary(
                    orderEntity.getId(),
                    orderEntity.getUserId(),
                    orderEntity.getOrderStatus(),
                    orderEntity.getTotalAmount()
            );
        }
        public static Summary from(OrderWrite orderEntity) {
            return new Summary(
                    orderEntity.getId(),
                    orderEntity.getUserId(),
                    orderEntity.getOrderStatus(),
                    orderEntity.getTotalAmount()
            );
        }
    }
    public record Detail(long orderId, long userId, LocalDateTime orderDate, String status, BigDecimal totalAmount,
                         List<OrderItemInfo> items) {
        public static Detail from(OrderRead orderEntity) {
            return new Detail(
                    orderEntity.getId(),
                    orderEntity.getUserId(),
                    orderEntity.getOrderDate(),
                    orderEntity.getOrderStatus(),
                    orderEntity.getTotalAmount(),
                    orderEntity.getItems().stream()
                            .map(item -> new OrderItemInfo(item.productId(), item.quantity()))
                            .collect(Collectors.toList())
            );
        }
        public static Detail from(OrderWrite orderEntity) {
            return new Detail(
                    orderEntity.getId(),
                    orderEntity.getUserId(),
                    orderEntity.getOrderDate(),
                    orderEntity.getOrderStatus(),
                    orderEntity.getTotalAmount(),
                    orderEntity.getItems().stream()
                            .map(item -> new OrderItemInfo(item.product().getId(), item.quantity()))
                            .collect(Collectors.toList())
            );
        }
    }
    public record OrderItemInfo(long productId, int quantity) {}
}
