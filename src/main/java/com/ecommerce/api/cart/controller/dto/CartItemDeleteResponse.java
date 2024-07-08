package com.ecommerce.api.cart.controller.dto;

import java.util.Map;

public record CartItemDeleteResponse(boolean success, String message, Map<String, Object> data) {
}
