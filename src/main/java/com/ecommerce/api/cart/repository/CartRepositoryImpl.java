package com.ecommerce.api.cart.repository;

import com.ecommerce.api.cart.service.repository.CartRepository;
import com.ecommerce.domain.Cart;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CartRepositoryImpl implements CartRepository {
    private final CartJPARepository cartJPARepository;

    public CartRepositoryImpl(CartJPARepository cartJPARepository) {
        this.cartJPARepository = cartJPARepository;
    }

    @Override
    public Optional<Cart> getById(Long cartId) {
        return cartJPARepository.findById(cartId);
    }

    @Override
    public Optional<Cart> saveAndGet(Cart cart) {
        Cart savedCart = cartJPARepository.save(cart);
        return cartJPARepository.findById(savedCart.getId());
    }




}
