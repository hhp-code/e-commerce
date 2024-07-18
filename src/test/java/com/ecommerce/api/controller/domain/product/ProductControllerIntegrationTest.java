package com.ecommerce.api.controller.domain.product;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private final Product product = new Product(1L,"Test Product", BigDecimal.valueOf(1000), 10);
    @BeforeEach
    void setup() {
        productService.saveAndGet(product);
        User user = new User("test", BigDecimal.valueOf(1000));
        userService.saveUser(user);
        Map<Product, Integer> orderItem = Map.of(product, 1);
        Order oder = new Order(user,orderItem);
        oder.finish();
        orderService.saveAndGet(oder);
    }



    @Test
    void getProductTest() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].id").value(product.getId()))
                .andExpect(jsonPath("$.products[0].name").value("Test Product"));
    }

    @Test
    void getProductDetailTest() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getPopularProductsTest() throws Exception {
        mockMvc.perform(get("/api/products/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].id").value(product.getId()))
                .andExpect(jsonPath("$.products[0].name").value("Test Product"));
    }

}