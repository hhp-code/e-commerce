package com.ecommerce.domain.order.service;

import com.ecommerce.domain.product.Product;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class OrderCommand {
    public record Create(long id, Map<Long, Integer> items) {
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
