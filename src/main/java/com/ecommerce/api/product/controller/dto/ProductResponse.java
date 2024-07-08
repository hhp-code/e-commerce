package com.ecommerce.api.product.controller.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price, int quantity) {
}
