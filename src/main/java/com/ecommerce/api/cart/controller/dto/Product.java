package com.ecommerce.api.cart.controller.dto;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price) {
}
