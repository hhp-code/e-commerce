package com.ecommerce.api.cart.service.repository;

import com.ecommerce.domain.Cart;

import java.util.Optional;

public interface CartRepository {
    Optional<Cart> getById(Long nonExistentCartId);

    Optional<Cart> saveAndGet(Cart any);


}
