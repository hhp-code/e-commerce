package com.ecommerce.api.cart.service;

import com.ecommerce.api.cart.service.repository.CartRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.domain.Cart;
import com.ecommerce.domain.CartItem;
import com.ecommerce.domain.Product;
import com.ecommerce.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        userRepository.save(testUser);

        testProduct = new Product("Test Product", BigDecimal.ONE, 100);
        productRepository.save(testProduct);
    }

    @Test
    void testGetCart() {
        Cart testCart = new Cart();
        Cart retrievedCart = cartService.getCart(testCart.getId());
        assertNotNull(retrievedCart);
        assertEquals(testCart.getId(), retrievedCart.getId());
    }

    @Test
    void testAddItemToCart() {
        CartCommand.Add addCommand = new CartCommand.Add(testUser.getId(), testProduct.getId(), 2);
        Cart updatedCart = cartService.addItemToCart(addCommand);

        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getCartItems().size());
        CartItem addedItem = updatedCart.getCartItems().getFirst();
        assertEquals(testProduct.getId(), addedItem.getProduct().getId());
        assertEquals(2, addedItem.getQuantity());
    }

    @Test
    void testRemoveItemFromCart() {
        // 먼저 아이템을 추가
        CartCommand.Add addCommand = new CartCommand.Add(testUser.getId(), testProduct.getId(), 1);
        cartService.addItemToCart(addCommand);

        // 아이템 제거
        Cart updatedCart = cartService.removeItemFromCart(testProduct.getId(), testUser.getId());

        assertNotNull(updatedCart);
        assertTrue(updatedCart.getCartItems().isEmpty());
    }

    @Autowired
    private CartRepository cartRepository;

    @Test
    void testUpdateCartItemQuantity() {
        // 먼저 아이템을 추가
        CartCommand.Add addCommand = new CartCommand.Add(testUser.getId(), testProduct.getId(), 1);
        Cart cart = cartService.addItemToCart(addCommand);
        System.out.println(cartRepository.getById(testUser.getId()).get().getCartItems().get(0).getQuantity());


        // 수량 업데이트
        CartCommand.Update updateCommand = new CartCommand.Update(testUser.getId(), testProduct.getId(), 3);
        Cart updatedCart = cartService.updateCartItemQuantity(updateCommand);
        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getCartItems().size());
        assertEquals(3, updatedCart.getCartItem(testProduct).get().getQuantity());
    }

    @Test
    void testAddItemToCartWithInvalidUser() {
        CartCommand.Add addCommand = new CartCommand.Add(999L, testProduct.getId(), 2);
        assertThrows(EntityNotFoundException.class, () -> cartService.addItemToCart(addCommand));
    }

    @Test
    void testAddItemToCartWithInvalidProduct() {
        CartCommand.Add addCommand = new CartCommand.Add(testUser.getId(), 999L, 2);
        assertThrows(EntityNotFoundException.class, () -> cartService.addItemToCart(addCommand));
    }
}