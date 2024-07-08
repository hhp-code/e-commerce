package com.ecommerce.api.cart.controller;

import java.time.LocalDateTime;
import java.util.List;

record CartResponse(long id, LocalDateTime lastUpdated, LocalDateTime expirationDate, List<CartItem> items) {
}
