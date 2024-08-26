package com.ecommerce.domain.order.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderQuery {
    public record GetOrder(long orderId) {}
    public record GetUserOrders(long userId) {}
}
