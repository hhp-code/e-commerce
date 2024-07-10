package com.ecommerce.api.cart.controller;

import com.ecommerce.api.cart.controller.dto.CartDto;
import com.ecommerce.api.cart.service.CartCommand;
import com.ecommerce.api.cart.service.CartService;
import com.ecommerce.domain.Cart;
import com.ecommerce.domain.CartItem;
import com.ecommerce.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @Test
    public void testGetCart() throws Exception {
        Long cartId = 1L;
        Cart mockCart = new Cart();

        when(cartService.getCart(cartId)).thenReturn(mockCart);

        mockMvc.perform(get("/api/carts/{id}", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    public void testAddItemToCart() throws Exception {
        Long productId = 1L;
        User user = new User("test", BigDecimal.ZERO);
        Cart cart = new Cart(user);
        CartDto.CartItemRequest request = new CartDto.CartItemRequest(2L,10);
        CartCommand.Add add = new CartCommand.Add(1L, 1L, 4);

        when(cartService.addItemToCart(add)).thenReturn(cart);

        mockMvc.perform(post("/api/carts/{productId}/items", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantity").value(request.quantity()));
    }

    @Test
    public void testRemoveItemFromCart() throws Exception {
        Long productId = 1L;
        Long userId = 1L;
        User user = new User("test", BigDecimal.ZERO);
        Cart mockCart = new Cart(user);

        when(cartService.removeItemFromCart(productId, userId)).thenReturn(mockCart);

        mockMvc.perform(delete("/api/cart/items/{productId}", productId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품이 장바구니에서 삭제되었습니다."))
                .andExpect(jsonPath("$.data.cartId").value(userId));
    }

    @Test
    public void testUpdateCartItemQuantity() throws Exception {
        Long productId = 1L;
        User user = new User("test", BigDecimal.ZERO);
        Cart cart = new Cart(user);
        CartDto.CartItemUpdateRequest request = new CartDto.CartItemUpdateRequest(3L,10);
        CartCommand.Update update = new CartCommand.Update(3L, 1L, 10);
        when(cartService.updateCartItemQuantity(update)).thenReturn(cart);

        mockMvc.perform(patch("/api/cart/items/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 수량이 변경되었습니다."))
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.newQuantity").value(request.quantity()));
    }
}