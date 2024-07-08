package com.ecommerce.api.product.controller;

import java.math.BigDecimal;

record ProductRequest(String name, BigDecimal price, int quantity) {
}
