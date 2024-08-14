package com.ecommerce.domain.order.command;

import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class OrderCommand {
    public record Create(long userId, List<OrderItemWrite> items) {
    }

    public record Add(long orderId, long productId, int quantity) {
    }

    public record Payment(long orderId) {
    }

    public record Cancel(long orderId) {
    }

    public record Delete(long orderId, long productId) {
    }
}
