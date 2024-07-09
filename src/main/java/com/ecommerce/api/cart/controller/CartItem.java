package com.ecommerce.api.cart.controller;

import com.ecommerce.api.cart.controller.dto.ProductRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class CartItem {
    @Setter
    private int quantity;
    private final ProductRequest product;

    public CartItem(Long productId, int quantity) {
        this.quantity = quantity;
        this.product = new ProductRequest(productId, "Sample ProductRequest", BigDecimal.valueOf(10000));
    }


}
