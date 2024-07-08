package com.ecommerce.api.cart.controller.dto;

import com.ecommerce.api.cart.controller.CartItem;

import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(long id, LocalDateTime lastUpdated, LocalDateTime expirationDate, List<CartItem> items) {
}
