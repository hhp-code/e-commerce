package com.ecommerce.api.cart.service;

import com.ecommerce.api.cart.service.repository.CartRepository;
import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.api.domain.Cart;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceUnitTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCart_WithExistingId_ShouldReturnCart() {
        Long cartId = 1L;
        Cart expectedCart = new Cart();
        when(cartRepository.getById(cartId)).thenReturn(Optional.of(expectedCart));

        Cart result = cartService.getCart(cartId);

        assertNotNull(result);
        verify(cartRepository).getById(cartId);
    }

    @Test
    void getCart_WithNonExistentId_ShouldThrowException() {
        Long nonExistentCartId = 999L;
        when(cartRepository.getById(nonExistentCartId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.getCart(nonExistentCartId));
        verify(cartRepository).getById(nonExistentCartId);
    }





}