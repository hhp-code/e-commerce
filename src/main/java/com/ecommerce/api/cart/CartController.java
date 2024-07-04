package com.ecommerce.api.cart;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CartController {

    private final Map<Long, Cart> carts = new HashMap<>();

    @GetMapping("/carts/{id}")
    public ResponseEntity<?> getCart(@PathVariable Long id) {
        Cart cart = carts.get(id);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        CartResponse cartResponse = new CartResponse(
                id,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                cart.items()
        );
        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping("/carts/{productId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable Long productId, @RequestBody CartItemRequest request) {
        Cart cart = carts.computeIfAbsent(productId, l -> new Cart(new ArrayList<>()));

        if (request.quantity() < 1 || request.quantity() > 10) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "상품 수량은 1개 이상 10개 이하여야 합니다.");
            errorResponse.put("errorCode", "INVALID_PRODUCT_QUANTITY");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        CartItem newItem = new CartItem(request.productId(), request.quantity());
        CartItemResponse response = new CartItemResponse(1L, 30,LocalDateTime.now(),newItem);
        cart.items().add(newItem);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cart/items/{productId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long productId, @RequestParam Long userId) {
        Cart cart = carts.get(userId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        boolean removed = cart.items().removeIf(item -> item.getProduct().id().equals(productId));
        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        CartItemDeleteResponse cartItemDeleteResponse = new CartItemDeleteResponse(true, "상품이 장바구니에서 삭제되었습니다.", Map.of("cartId", userId, "totalItems", cart.items().size()));
        return ResponseEntity.ok(cartItemDeleteResponse);
    }


    @PatchMapping("/cart/items/{productId}")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Long productId, @RequestBody CartItemUpdateRequest request) {
        Cart cart = carts.get(request.userId());
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        CartItemUpdateResponse cartItemUpdateResponse = new CartItemUpdateResponse(true, "상품 수량이 변경되었습니다.", Map.of("id", productId, "newQuantity", request.quantity(), "totalAmount", calculateTotalAmount(cart)));
        return ResponseEntity.ok(cartItemUpdateResponse);
    }


    private BigDecimal calculateTotalAmount(Cart cart) {
        return cart.items().stream()
                .map(item -> item.getProduct().price().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    record CartItemRequest(Long productId, int quantity) { }

    record CartItemUpdateRequest(Long userId, int quantity) { }

    record CartItemDeleteResponse(boolean success, String message, Map<String, Object> data) { }

    record CartItemResponse(Long id, int quantity, LocalDateTime addedDate, CartItem item ) { }

    private record Product(Long id, String name, BigDecimal price) { }

    private record Cart(  List<CartItem> items) { }

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