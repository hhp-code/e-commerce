package com.ecommerce.api.cart.controller.dto;

import java.math.BigDecimal;

public record ProductRequest(Long id, String name, BigDecimal price) {
}
