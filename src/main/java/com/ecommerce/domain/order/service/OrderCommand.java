package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.OrderItem;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class OrderCommand {
    public record Create(long id, List<OrderItem> items) {
    }
    public record Search(long id) {
    }
    public record Add(long userId, long productId, int quantity) {
    }
    public record Payment(long userId, long orderId) {
    }
    public record Cancel(long userId, long orderId) {
    }
    public record Delete(long orderId, long productId) {
    }
}
