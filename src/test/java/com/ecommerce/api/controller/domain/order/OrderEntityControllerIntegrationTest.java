package com.ecommerce.api.controller.domain.order;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("cleanser")
@Transactional
class OrderEntityControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private User testUser;
    private Product testProduct;
    private OrderDto.OrderCreateRequest orderCreateRequest;


    @Test
    @DisplayName("주문 생성")
    void createOrder() throws Exception {
        testProduct = productService.saveAndGet(new Product("testProduct1", BigDecimal.valueOf(100), 10));
        testUser = userService.saveUser(new User("testUser1", BigDecimal.valueOf(1000)));
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 1);
        orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);
        OrderDto.OrderCreateRequest orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), Map.of(testProduct.getId(), 3));
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARED"));
    }

    @Test
    @DisplayName("주문 생성 - 재고 부족")
    void createOrderWithInsufficientStock() throws Exception {
        testProduct = productService.saveAndGet(new Product("testProduct1", BigDecimal.valueOf(100), 10));
        testUser = userService.saveUser(new User("testUser1", BigDecimal.valueOf(1000)));
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 11);
        orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);
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
        Product testProduct = productService.saveAndGet(new Product("testProduct2", BigDecimal.valueOf(100), 10));
        User testUser = userService.saveUser(new User("testUser2", BigDecimal.valueOf(1000)));
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 1);
        orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);
        ResultActions perform = mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(orderCreateRequest)));
        MvcResult authorization = perform.andReturn();
        String contentAsString = authorization.getResponse().getContentAsString();
        OrderDto.OrderDetailResponse orderDetailResponse = objectMapper.readValue(contentAsString, OrderDto.OrderDetailResponse.class);

        mockMvc.perform(get("/api/orders/{orderId}", orderDetailResponse.id())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDetailResponse.id()))
                .andExpect(jsonPath("$.status").value("PREPARED"));
    }

    @Test
    @DisplayName("주문조회 - 주문 없음")
    void getOrderWithNonExistentOrder() throws Exception {

        mockMvc.perform(get("/api/orders/{orderId}", 2L)
                        .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("주문조회 - 잘못된 요청")
    void getOrderWithInvalidRequest() throws Exception {

        mockMvc.perform(get("/api/orders/{orderId}", -1L)
                        .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("결제 요청")
    void payOrder() throws Exception {
        testProduct = productService.saveAndGet(new Product("testProduct3", BigDecimal.valueOf(100), 10));
        testUser = userService.saveUser(new User("testUser3", BigDecimal.valueOf(1000)));
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 1);
        orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);
        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(orderCreateRequest)));
        OrderDto.OrderPayRequest orderPayRequest = new OrderDto.OrderPayRequest( 1L);
        mockMvc.perform(post("/api/orders/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("결제 요청 - 주문 없음")
    void payOrderWithNonExistentOrder() throws Exception {
        OrderDto.OrderPayRequest orderPayRequest = new OrderDto.OrderPayRequest(999L);
        mockMvc.perform(post("/api/orders/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("결제 요청 - 잘못된 요청")
    void payOrderWithInvalidRequest() throws Exception {
        testProduct = productService.saveAndGet(new Product("testProduct4", BigDecimal.valueOf(100), 10));
        testUser = userService.saveUser(new User("testUser4", BigDecimal.valueOf(1000)));
        Map<Long, Integer> createOrderRequest = Map.of(testProduct.getId(), 1);
        orderCreateRequest = new OrderDto.OrderCreateRequest(testUser.getId(), createOrderRequest);
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderCreateRequest)));

        OrderDto.OrderPayRequest orderPayRequest = new OrderDto.OrderPayRequest(-1L);
        mockMvc.perform(post("/api/orders/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }







}