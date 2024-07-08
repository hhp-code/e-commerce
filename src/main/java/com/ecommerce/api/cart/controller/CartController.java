package com.ecommerce.api.cart.controller;

import com.ecommerce.api.cart.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
@Tag(name = "cart", description = "장바구니 관련 API")
@RestController
@RequestMapping("/api")
public class CartController {


    @GetMapping("/carts/{id}")
    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니를 조회합니다.")
    public CartResponse getCart(@PathVariable Long id) {
        return new CartResponse(
                id,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                new ArrayList<>()
        );
    }

    @PostMapping("/carts/{productId}/items")
    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    public CartItemResponse addItemToCart(@PathVariable Long productId, @RequestBody CartItemRequest request) {
        CartItem newItem = new CartItem(request.productId(), request.quantity());
        return new CartItemResponse(productId, request.quantity(), LocalDateTime.now(), newItem);
    }

    @DeleteMapping("/cart/items/{productId}")
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다.")
    public CartItemDeleteResponse removeItemFromCart(@PathVariable Long productId, @RequestParam Long userId) {
        return new CartItemDeleteResponse(true, "상품이 장바구니에서 삭제되었습니다.", Map.of("cartId", userId, "totalItems", 0));
    }


    @PatchMapping("/cart/items/{productId}")
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 담긴 상품의 수량을 변경합니다.")
    public CartItemUpdateResponse updateCartItemQuantity(@PathVariable Long productId, @RequestBody CartItemUpdateRequest request) {
        return new CartItemUpdateResponse(true, "상품 수량이 변경되었습니다.", Map.of("id", productId, "newQuantity", request.quantity(), "totalAmount", 0));
    }

}

