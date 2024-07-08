package com.ecommerce.api.cart.controller.dto;

public record CartItemUpdateRequest(Long userId, int quantity) {
}
