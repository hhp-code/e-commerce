package com.ecommerce.domain.order.service;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class OrderQuery {
    public record GetOrder(long orderId) {}
    public record GetUserOrders(long userId) {}
    public record GetOrdersInDateRange(LocalDate startDate, LocalDate endDate) {}
}
