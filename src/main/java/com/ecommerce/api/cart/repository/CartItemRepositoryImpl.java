package com.ecommerce.api.cart.repository;

import com.ecommerce.api.cart.service.repository.CartItemRepository;
import com.ecommerce.api.domain.CartItem;
import org.springframework.stereotype.Repository;

@Repository
public class CartItemRepositoryImpl implements CartItemRepository {
    private final CartItemJPARepository cartItemJPARepository;

    public CartItemRepositoryImpl(CartItemJPARepository cartItemJPARepository) {
        this.cartItemJPARepository = cartItemJPARepository;

    }

    @Override
    public void save(CartItem cartItem) {
        cartItemJPARepository.save(cartItem);
    }
}
