package com.ecommerce.domain.order;

import com.ecommerce.domain.order.orderitem.OrderItemRead;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
public class OrderRead {
    private final Long id;
    private final Long userId;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final List<OrderItemRead> items;
    private final LocalDateTime orderDate;

    public OrderRead(Long id, Long userId, String status, BigDecimal totalAmount, List<OrderItemRead> items, LocalDateTime orderDate) {
        this.id = id;
        this.userId = userId;
        this.status = OrderStatus.valueOf(status);
        this.totalAmount = totalAmount;
        this.items = items;
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return status.name();
    }
}

