package com.ecommerce.api.order.service;

import com.ecommerce.api.domain.CartItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class OrderCommand {
    public record Create(long id, List<CartItem> items) {
    }
    public record Search(long id) {
    }
    public record Add(long userId, long productId, int quantity) {
    }
    public record Payment(long orderId, BigDecimal amount) {
    }
}
