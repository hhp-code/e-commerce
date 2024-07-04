package com.ecommerce.api.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testListProducts() throws Exception {
        mockMvc.perform(get("/api/products").header("Authorization", "Bearer valid12341234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].price").exists())
                .andExpect(jsonPath("$[0].quantity").exists());
    }

    @Test
    public void testGetProduct() throws Exception {
        mockMvc.perform(get("/api/products/1").header("Authorization", "Bearer valid12341234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.quantity").exists());
    }

    @Test
    public void testCreateProduct() throws Exception {
        ProductController.ProductRequest request = new ProductController.ProductRequest("New Product", BigDecimal.valueOf(150), 25);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid12341234")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(150))
                .andExpect(jsonPath("$.quantity").value(25));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        ProductController.ProductRequest request = new ProductController.ProductRequest("Updated Product", BigDecimal.valueOf(200), 30);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid12341234")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(200))
                .andExpect(jsonPath("$.quantity").value(30));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization","Bearer valid12341234")
                )
                .andExpect(status().isNoContent());
    }
}