package com.ecommerce.api.cart.controller.dto;

public record CartItemRequest(Long productId, int quantity) {
}
