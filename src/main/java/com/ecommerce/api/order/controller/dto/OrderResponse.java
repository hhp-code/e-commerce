package com.ecommerce.api.order.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, LocalDateTime orderDate, BigDecimal regularPrice, BigDecimal salePrice,
                            BigDecimal sellingPrice
        , String status, Boolean isDeleted, LocalDateTime deletedAt, List<OrderItem> items) {
}
