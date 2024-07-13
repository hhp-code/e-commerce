package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.OrderItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class OrderCommand {
    public record Create(long id, List<OrderItem> items) {
    }
    public record Search(long id) {
    }
    public record Add(long userId, long productId, int quantity) {
    }
    public record Payment(long orderId, BigDecimal amount) {
    }
    public record Cancel(long orderId) {
    }
}
