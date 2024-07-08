package com.ecommerce.api.product.controller.dto;

import java.math.BigDecimal;

public record ProductRequest(String name, BigDecimal price, int quantity) {
}
