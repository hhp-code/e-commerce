package com.ecommerce.api.product.controller;

import java.math.BigDecimal;

record ProductResponse(Long id, String name, BigDecimal price, int quantity) {
}
