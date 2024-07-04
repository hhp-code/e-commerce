package com.ecommerce.api.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCart() throws Exception {
        mockMvc.perform(get("/api/carts/1").header("Authorization", "Bearer valid12341234"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddItemToCart() throws Exception {
        CartController.CartItemRequest request = new CartController.CartItemRequest(1L,2);

        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid123123123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(30));
    }

    @Test
    public void testAddItemToCartInvalidQuantity() throws Exception {
        CartController.CartItemRequest request = new CartController.CartItemRequest(1L,11);

        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid123123123123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("상품 수량은 1개 이상 10개 이하여야 합니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_PRODUCT_QUANTITY"));
    }

    @Test
    public void testRemoveItemFromCart() throws Exception {
        CartController.CartItemRequest addRequest = new CartController.CartItemRequest(1L,2);

        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid123123123")
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());
        long productId = 1L;
        long userId = 1L;
        // Then, remove the item
        mockMvc.perform(delete("/api/cart/items/{productId}", productId)
                        .param("userId", String.valueOf(userId))
                        .header("Authorization", "Bearer valid123123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품이 장바구니에서 삭제되었습니다."))
                .andExpect(jsonPath("$.data.cartId").value(1))
                .andExpect(jsonPath("$.data.totalItems").value(0));
    }

    @Test
    public void testUpdateCartItemQuantity() throws Exception {
        CartController.CartItemRequest addRequest = new CartController.CartItemRequest(1L,2);

        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid123123123")
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        CartController.CartItemUpdateRequest updateRequest = new CartController.CartItemUpdateRequest(1L,5);

        mockMvc.perform(patch("/api/cart/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer valid123123")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 수량이 변경되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.newQuantity").value(5))
                .andExpect(jsonPath("$.data.totalAmount").value(40000));
    }
}