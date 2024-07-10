package com.ecommerce.api.cart.controller;

import com.ecommerce.api.cart.controller.dto.*;
import com.ecommerce.api.cart.service.CartCommand;
import com.ecommerce.api.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Tag(name = "cart", description = "장바구니 관련 API")
@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/carts/{id}")
    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니를 조회합니다.")
    public CartDto.CartResponse getCart(@PathVariable Long id) {
        return CartMapper.toCartResponse(
                cartService.getCart(id)
        );
    }

    @PostMapping("/carts/{userId}/items")
    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    public CartDto.CartResponse addItemToCart(@PathVariable Long userId, @RequestBody CartDto.CartItemRequest request) {
        return CartMapper.toCartResponse(
                cartService.addItemToCart(CartMapper.toAddItem(userId,request))
        );
    }

    @DeleteMapping("/cart/items/{productId}")
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다.")
    public CartDto.CartItemRemoveResponse removeItemFromCart(@PathVariable Long productId, @RequestParam Long userId) {
        return CartMapper.toCartItemDeleteResponse(
                cartService.removeItemFromCart(productId, userId)
        );
    }


    @PatchMapping("/cart/items/{productId}")
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 담긴 상품의 수량을 변경합니다.")
    public CartDto.CartItemUpdateResponse updateCartItemQuantity(@PathVariable Long productId, @RequestBody CartDto.CartItemUpdateRequest request) {
        return CartMapper.toCartItemUpdateResponse(
                cartService.updateCartItemQuantity(CartMapper.toUpdateItem(productId,request))
        );
    }

}

