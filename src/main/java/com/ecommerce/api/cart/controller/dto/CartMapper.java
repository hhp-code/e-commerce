package com.ecommerce.api.cart.controller.dto;

import com.ecommerce.api.cart.service.CartCommand;
import com.ecommerce.domain.Cart;

import java.util.Map;

public class CartMapper {

    public static CartCommand.Add toAddItem(long userId, CartDto.CartItemRequest request ) {
        return new CartCommand.Add(userId, request.productId(), request.quantity());
    }

    public static CartDto.CartResponse toCartResponse(Cart cart) {
        return new CartDto.CartResponse(cart.getId(), cart.getLastUpdated(), cart.getExpirationDate(), cart.getCartItems());
    }

    public static CartDto.CartItemRemoveResponse toCartItemDeleteResponse(Cart cart) {
        if(cart !=null){
            Map<String, Object> data = Map.of("id", cart.getId(), "totalAmount", cart.getCartItems().size());
            return new CartDto.CartItemRemoveResponse(true, "상품이 삭제되었습니다.", data);
        }
        return new CartDto.CartItemRemoveResponse(false, "상품이 삭제되지 않았습니다.", Map.of());
    }

    public static CartDto.CartItemUpdateResponse toCartItemUpdateResponse(Cart cart) {
        return new CartDto.CartItemUpdateResponse(true, "상품 수량이 변경되었습니다.", Map.of("id", cart.getId(), "totalAmount", cart.getCartItems().size()));
    }

    public static CartCommand.Update toUpdateItem(Long productId, CartDto.CartItemUpdateRequest request) {
        return new CartCommand.Update(request.userId(),productId, request.quantity());
    }
}
