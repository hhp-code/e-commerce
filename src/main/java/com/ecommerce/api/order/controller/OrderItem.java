package com.ecommerce.api.order.controller;

import java.math.BigDecimal;

record OrderItem(Long productId, String productName, int quantity, BigDecimal price) {
}
