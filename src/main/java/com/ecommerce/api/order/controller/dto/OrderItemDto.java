package com.ecommerce.api.order.controller.dto;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class OrderItemDto {
    public record OrderItemResponse(
            Long id,
            Long productId,
            String productName,
            int quantity,
            BigDecimal price
    ) {
    }
    public record OrderItemCreateRequest(
            Long productId,
            int quantity
    ) {}
}
