package com.ecommerce.api.controller.domain.order;

import com.ecommerce.api.controller.domain.order.dto.OrderDto;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private User testUser;
    private Product testProduct;
    private OrderDto.OrderCreateRequest orderCreateRequest;

    @BeforeEach
    void setUp() {
        testProduct = productService.saveAndGet(new Product("testProduct", BigDecimal.valueOf(100), 10));
        testUser = userService.saveUser(new User("testUser", BigDecimal.valueOf(1000)));
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 1);
        orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);
    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARED"));
    }

    @Test
    @DisplayName("주문 생성 - 재고 부족")
    void createOrderWithInsufficientStock() throws Exception {
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 11);
        OrderDto.OrderCreateRequest orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("상품의 재고가 부족합니다."));
    }

    @Test
    @DisplayName("주문조회")
    void getOrder() throws Exception {

        mockMvc.perform(post("/api/orders/{orderId}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PREPARED"));
    }

    @Test
    @DisplayName("주문조회 - 주문 없음")
    void getOrderWithNonExistentOrder() throws Exception {

        mockMvc.perform(post("/api/orders/{orderId}", 2L)
                        .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("주문조회 - 잘못된 요청")
    void getOrderWithInvalidRequest() throws Exception {

        mockMvc.perform(post("/api/orders/{orderId}", -1L)
                        .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("결제 요청")
    void payOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)));

        OrderDto.OrderPayRequest orderPayRequest = new OrderDto.OrderPayRequest(testUser.getId(), 1L);
        mockMvc.perform(post("/api/orders/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("결제 요청 - 주문 없음")
    void payOrderWithNonExistentOrder() throws Exception {
        OrderDto.OrderPayRequest orderPayRequest = new OrderDto.OrderPayRequest(testUser.getId(), 999L);
        mockMvc.perform(post("/api/orders/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("결제 요청 - 잘못된 요청")
    void payOrderWithInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)));

        OrderDto.OrderPayRequest orderPayRequest = new OrderDto.OrderPayRequest(testUser.getId(), -1L);
        mockMvc.perform(post("/api/orders/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)));

        OrderDto.OrderCancelRequest orderCancelRequest = new OrderDto.OrderCancelRequest(testUser.getId(), 1L);
        mockMvc.perform(patch("/api/orders/cancel")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("주문 취소 - 주문 없음")
    void cancelOrderWithNonExistentOrder() throws Exception {
        OrderDto.OrderCancelRequest orderCancelRequest = new OrderDto.OrderCancelRequest(testUser.getId(), 1L);
        mockMvc.perform(patch("/api/orders/cancel")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCancelRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }





}