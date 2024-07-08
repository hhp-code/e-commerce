package com.ecommerce.api.cart.controller;

import java.time.LocalDateTime;

record CartItemResponse(Long id, int quantity, LocalDateTime addedDate, CartItem item) {
}
