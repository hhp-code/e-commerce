package com.ecommerce.api.controller.domain.product;

import com.ecommerce.api.usecase.PopularProductUseCase;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private PopularProductUseCase popularProductCase;

    @Test
    @DisplayName("상품 목록 조회 실패 - 빈 목록")
    void getProductsFailure() throws Exception {
        // given
        when(productService.getProducts()).thenThrow(new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("상품 상세 조회 실패 - 존재하지 않는 상품")
    void getProductFailure() throws Exception {
        // given
        Long nonExistentProductId = 999L;
        when(productService.getProduct(nonExistentProductId)).thenThrow(new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/products/{productId}", nonExistentProductId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("인기 상품 조회 실패 - 빈 목록")
    void getPopularProductsFailure() throws Exception {
        // given
        when(productService.getPopularProducts()).thenThrow(new IllegalArgumentException("인기 상품을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/products/popular"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.products").exists());
    }
    @Test
    @DisplayName("인기 상품 조회")
    void getPopularProducts_Success() throws Exception {
        Product product = new Product("test", BigDecimal.ONE,1000);
        List<Product> products = List.of(product);
        when(popularProductCase.getPopularProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products/popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value(product.getName()));
    }
}