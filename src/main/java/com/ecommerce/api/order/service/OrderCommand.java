package com.ecommerce.api.order.service;

import com.ecommerce.api.domain.OrderItem;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class OrderCommand {
    public record Create(long id, List<OrderItem> items) {

    }
    public record Search(long id) {
    }
    public record OrderItemCommand(long productId, int quantity) {
    }
}
