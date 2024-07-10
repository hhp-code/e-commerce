package com.ecommerce.api.cart.controller;

import com.ecommerce.api.cart.service.CartCommand;
import com.ecommerce.api.cart.service.CartService;
import com.ecommerce.api.domain.Cart;
import com.ecommerce.api.domain.CartItem;
import com.ecommerce.api.domain.Product;
import com.ecommerce.api.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Test
    public void testGetCart() throws Exception {
        Long cartId = 1L;
        User user = new User("testUser", BigDecimal.valueOf(10));
        Cart cart = new Cart();
        when(cartService.getCart(cartId)).thenReturn(cart);

        mockMvc.perform(get("/api/carts/{id}", cartId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastUpdated").exists())
                .andExpect(jsonPath("$.expirationDate").exists())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    public void testAddItemToCart() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", BigDecimal.valueOf(10));
        Cart cart = new Cart();
        when(cartService.addItemToCart(any(CartCommand.Add.class))).thenReturn(cart);

        mockMvc.perform(post("/api/carts/{userId}/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastUpdated").exists())
                .andExpect(jsonPath("$.expirationDate").exists())
                .andExpect(jsonPath("$.items").isArray());
    }


    @Test
    public void testUpdateCartItemQuantity() throws Exception {
        Long productId = 1L;
        Cart cart = new Cart();
        CartItem cartItem = new CartItem(new Product("testProduct", BigDecimal.valueOf(10), 100), 2);
        cart.addCartItem(cartItem);
        User user = new User("testUser", BigDecimal.valueOf(10));
        user.setCart(cart);

        when(cartService.updateCartItemQuantity(any(CartCommand.Update.class))).thenReturn(cart);

        mockMvc.perform(patch("/api/cart/items/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"quantity\":3}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists());
    }
}