package com.ecommerce.api.cart.controller;

import java.math.BigDecimal;

record Product(Long id, String name, BigDecimal price) {
}
