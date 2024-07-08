package com.ecommerce.api.order.controller;

import com.ecommerce.api.order.controller.dto.OrderRequest;
import com.ecommerce.api.order.controller.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Tag(name = "order", description = "주문 관련 API")
@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/orders")
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return new OrderResponse(1L, LocalDateTime.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(0), BigDecimal.valueOf(200), "PENDING", false, null, request.items());
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "주문 조회", description = "주문을 조회합니다.")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return new OrderResponse(1L, LocalDateTime.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(0), BigDecimal.valueOf(200), "PENDING", false, null, Collections.emptyList());
    }

    @GetMapping("/orders")
    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회합니다.")
    public ResponseEntity<List<OrderResponse>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<OrderResponse> orders = Arrays.asList(
                new OrderResponse(1L, LocalDateTime.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(0), BigDecimal.valueOf(200), "PENDING", false, null, Collections.emptyList()),
                new OrderResponse(2L, LocalDateTime.now(), BigDecimal.valueOf(300), BigDecimal.valueOf(0), BigDecimal.valueOf(300), "PENDING", false, null, Collections.emptyList())
        );
        return ResponseEntity.ok(orders);
    }

}




