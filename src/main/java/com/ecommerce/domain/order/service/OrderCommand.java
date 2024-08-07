package com.ecommerce.domain.order.service;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class OrderCommand {
    public record Create(long userId, Map<Long, Integer> items) {
    }
    public record Search(long orderId) {
    }
    public record Add(long orderId, long productId, int quantity) {
    }
    public record Payment( long orderId) {
    }
    public record Cancel(long orderId) {
    }
    public record Delete(long orderId, long productId) {
    }
}
