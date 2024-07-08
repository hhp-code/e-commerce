package com.ecommerce.api.cart.controller;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class CartItem {
    @Setter
    private int quantity;
    private final Product product;

    public CartItem(Long productId, int quantity) {
        this.quantity = quantity;
        this.product = new Product(productId, "Sample Product", BigDecimal.valueOf(10000));
    }


}
