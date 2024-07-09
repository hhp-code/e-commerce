package com.ecommerce.api.order.controller;

import com.ecommerce.api.order.controller.dto.OrderDto;
import com.ecommerce.api.order.service.OrderService;
import com.ecommerce.domain.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;


    @Test
    @DisplayName("주문 생성 실패 - 최대 주문 수량 초과")
    void createOrder_ExceedMaxQuantity_ShouldFail() throws Exception {
        List<OrderItem> temp = new ArrayList<>();
        for(int i =0 ; i < 11; i++){
            temp.add(new OrderItem(1, BigDecimal.valueOf(1000)));
        }
        OrderDto.OrderCreateRequest orderCreateRequest = new OrderDto.OrderCreateRequest(1L, temp);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("주문 수량은 최대 10개까지 가능합니다."));
    }

    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
    void getOrder_NonExistentOrder_ShouldFail() throws Exception {
        Long nonExistentOrderId = 9999L;
        when(orderService.getOrder(nonExistentOrderId)).thenThrow(new RuntimeException("주문을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/orders/{orderId}", nonExistentOrderId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("주문 목록 조회 실패 - 잘못된 요청 파라미터")
    void listOrders_InvalidRequest_ShouldFail() throws Exception {
        OrderDto.OrderCreateRequest orderCreateRequest = new OrderDto.OrderCreateRequest(-1L, List.of());
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 번호는 0 이상이어야 합니다."));
    }
}