package com.ecommerce.api.cart.controller.dto;

import com.ecommerce.api.cart.controller.CartItem;

import java.time.LocalDateTime;

public record CartItemResponse(Long id, int quantity, LocalDateTime addedDate, CartItem item) {
}
