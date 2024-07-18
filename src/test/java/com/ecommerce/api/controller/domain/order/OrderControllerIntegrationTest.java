package com.ecommerce.api.controller.domain.order;

import com.ecommerce.api.controller.domain.order.dto.OrderDto;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<Long, Integer> testOrderItem = Map.of(1L, 1);
    private final OrderDto.OrderCreateRequest createRequest = new OrderDto.OrderCreateRequest(1, testOrderItem);
    private final OrderDto.OrderAddItemRequest addItemRequest = new OrderDto.OrderAddItemRequest(1, 1);
    private final OrderDto.OrderListRequest listRequest = new OrderDto.OrderListRequest(1);
    private final OrderDto.OrderPayRequest payRequest = new OrderDto.OrderPayRequest(1L,1L);
    private final OrderDto.OrderCreateRequest forCancelRequest = new OrderDto.OrderCreateRequest(1, testOrderItem);
    private final OrderDto.OrderCancelRequest cancelRequest = new OrderDto.OrderCancelRequest(1L,1L);

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @BeforeEach
    void setup(){
        userService.saveUser(new User("test",BigDecimal.valueOf(1000)));
        productService.saveAndGet(new Product("test",BigDecimal.valueOf(1000),10));
    }


    @Test
    void createOrderTest() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PREPARED"))
                .andReturn();
    }

    @Test
    void getOrderTest() throws Exception {

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto.OrderResponse createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                OrderDto.OrderResponse.class
        );

        // 생성된 주문 조회
        mockMvc.perform(get("/api/orders/{orderId}", createResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.id()));
    }

    @Test
    void addCartItemToOrderTest() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto.OrderResponse createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                OrderDto.OrderResponse.class
        );


        mockMvc.perform(patch("/api/orders/{orderId}/items", createResponse.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.id()));
    }

    @Test
    void listOrdersTest() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").isArray());
    }

    @Test
    void payOrderTest() throws Exception {

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto.OrderResponse createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                OrderDto.OrderResponse.class
        );


        mockMvc.perform(post("/api/orders/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.id()));
    }

    @Test
    void cancelOrderTest() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forCancelRequest)))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}