package com.ecommerce.api.cart.controller;

import com.ecommerce.api.cart.controller.dto.CartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCart() throws Exception {
        Long cartId = 1L;

        mockMvc.perform(get("/api/carts/{id}", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    public void testAddItemToCart() throws Exception {
        Long productId = 1L;
        CartDto.CartItemRequest request = new CartDto.CartItemRequest(2L, 10);

        mockMvc.perform(post("/api/carts/{productId}/items", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.quantity").value(request.quantity()));
    }

    @Test
    public void testRemoveItemFromCart() throws Exception {
        Long productId = 1L;
        Long userId = 1L;

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
        CartDto.CartItemUpdateRequest request = new CartDto.CartItemUpdateRequest(3L, 10);

        mockMvc.perform(patch("/api/cart/items/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 수량이 변경되었습니다."))
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.newQuantity").value(request.quantity()));
    }
}