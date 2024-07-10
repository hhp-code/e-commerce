package com.ecommerce.api.cart.service.repository;

import com.ecommerce.api.domain.CartItem;

public interface CartItemRepository {
    void save(CartItem cartItem);
}
