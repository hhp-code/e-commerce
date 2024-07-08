package com.ecommerce.api.cart;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CartController {


    @GetMapping("/carts/{id}")
    public CartResponse getCart(@PathVariable Long id) {
        return new CartResponse(
                id,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                new ArrayList<>()
        );
    }

    @PostMapping("/carts/{productId}/items")
    public CartItemResponse addItemToCart(@PathVariable Long productId, @RequestBody CartItemRequest request) {
        CartItem newItem = new CartItem(request.productId(), request.quantity());
        return new CartItemResponse(productId, request.quantity(), LocalDateTime.now(), newItem);
    }

    @DeleteMapping("/cart/items/{productId}")
    public CartItemDeleteResponse removeItemFromCart(@PathVariable Long productId, @RequestParam Long userId) {
        return new CartItemDeleteResponse(true, "상품이 장바구니에서 삭제되었습니다.", Map.of("cartId", userId, "totalItems", 0));
    }


    @PatchMapping("/cart/items/{productId}")
    public CartItemUpdateResponse updateCartItemQuantity(@PathVariable Long productId, @RequestBody CartItemUpdateRequest request) {
        return new CartItemUpdateResponse(true, "상품 수량이 변경되었습니다.", Map.of("id", productId, "newQuantity", request.quantity(), "totalAmount", 0));
    }


    record CartItemRequest(Long productId, int quantity) { }

    record CartItemUpdateRequest(Long userId, int quantity) { }

    record CartItemDeleteResponse(boolean success, String message, Map<String, Object> data) { }

    record CartItemResponse(Long id, int quantity, LocalDateTime addedDate, CartItem item ) { }

    private record Product(Long id, String name, BigDecimal price) { }

    private record CartItemUpdateResponse(boolean success, String message, Map<String, Object> data) { }

    private record CartResponse(long id, LocalDateTime lastUpdated, LocalDateTime expirationDate, List<CartItem> items){}
    @Getter
    private static class CartItem {
        @Setter
        private int quantity;

        private final Product product;

        public CartItem(Long productId, int quantity) {
            this.quantity = quantity;
            this.product = new Product(productId, "Sample Product", BigDecimal.valueOf(10000));
        }

    }
}