package com.ecommerce.api.cart.controller.dto;

import com.ecommerce.domain.CartItem;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@UtilityClass
public class CartDto {
    public record CartItemRemoveResponse(boolean success, String message, Map<String, Object> data) {
    }
    public record CartItemRequest(Long productId, int quantity) {
    }

    public record CartItemUpdateRequest(Long userId, int quantity) {
    }
    public record CartItemUpdateResponse(boolean success, String message, Map<String, Object> data) {
    }
    public record CartResponse( LocalDateTime lastUpdated, LocalDateTime expirationDate, List<CartItem> items) {
    }


}
