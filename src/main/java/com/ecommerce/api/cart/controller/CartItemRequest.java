package com.ecommerce.api.cart.controller;

record CartItemRequest(Long productId, int quantity) {
}
