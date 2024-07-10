package com.ecommerce.api.cart.service;

import com.ecommerce.api.cart.controller.dto.CartDto;
import com.ecommerce.api.cart.service.repository.CartRepository;
import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.domain.Cart;
import com.ecommerce.domain.User;
import com.ecommerce.domain.CartItem;
import com.ecommerce.domain.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        User user = new User("test", BigDecimal.ZERO);
        Cart expectedCart = new Cart(user);
        when(cartRepository.getById(cartId)).thenReturn(Optional.of(expectedCart));

        Cart result = cartService.getCart(cartId);

        assertNotNull(result);
        assertEquals(cartId, result.getId());
        assertEquals(user, result.getUser());
        verify(cartRepository).getById(cartId);
    }

    @Test
    void getCart_WithNonExistentId_ShouldThrowException() {
        Long nonExistentCartId = 999L;
        when(cartRepository.getById(nonExistentCartId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.getCart(nonExistentCartId));
        verify(cartRepository).getById(nonExistentCartId);
    }

    @Test
    void addItemToCart_WithValidData_ShouldAddItem() {
        Long userId = 1L;
        Long productId = 1L;
        int quantity = 2;
        User user = new User();
        Product product = new Product();
        Cart cart = new Cart(user);
        CartCommand.Add addCommand = new CartCommand.Add(userId, productId, quantity);

        when(cartRepository.getById(userId)).thenReturn(Optional.of(cart));
        when(productRepository.getProduct(productId)).thenReturn(Optional.of(product));
        when(cartRepository.saveAndGet(any(Cart.class))).thenReturn(Optional.of(cart));

        Cart result = cartService.addItemToCart(addCommand);

        assertNotNull(result);
        assertEquals(1, result.getCartItems().size());
        assertTrue(result.getLastUpdated().isBefore(LocalDateTime.now()) || result.getLastUpdated().isEqual(LocalDateTime.now()));
        verify(cartRepository).getById(userId);
        verify(productRepository).getProduct(productId);
        verify(cartRepository).saveAndGet(cart);
    }

    @Test
    void removeItemFromCart_WithExistingItem_ShouldRemoveItem() {
        Long userId = 1L;
        Long productId = 1L;
        User user = new User();
        Product product = new Product();
        Cart cart = new Cart(user);
        CartItem cartItem = new CartItem(cart, product, 1);
        cart.addCartItem(cartItem);

        when(cartRepository.getById(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.saveAndGet(any(Cart.class))).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(productId, userId);

        assertTrue(cart.getCartItems().isEmpty());
        assertTrue(cart.getLastUpdated().isBefore(LocalDateTime.now()) || cart.getLastUpdated().isEqual(LocalDateTime.now()));
        verify(cartRepository).getById(userId);
        verify(cartRepository).saveAndGet(cart);
    }

    @Test
    void updateCartItemQuantity_WithValidData_ShouldUpdateQuantity() {
        Long cartId = 1L;
        Long productId = 1L;
        int newQuantity = 3;
        User user = new User("test", BigDecimal.ZERO);
        Product product = new Product();
        Cart cart = new Cart(user);
        CartItem cartItem = new CartItem(cart, product, 1);
        cart.addCartItem(cartItem);
        CartCommand.Update updateCommand = new CartCommand.Update(cartId, productId, newQuantity);

        CartDto.CartItemUpdateRequest updateRequest = new CartDto.CartItemUpdateRequest(productId, newQuantity);

        when(cartRepository.getById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.saveAndGet(any(Cart.class))).thenReturn(Optional.of(cart));

        Cart result = cartService.updateCartItemQuantity(updateCommand);
        CartItem updatedCartItem = result.getCartItem(product).orElseThrow();

        assertNotNull(result);
        assertEquals(newQuantity, updatedCartItem.getQuantity());
        verify(cartRepository).getById(cartId);
        verify(cartRepository).saveAndGet(cart);
    }
}