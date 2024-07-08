package com.ecommerce.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/orders")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return new OrderResponse(1L, LocalDateTime.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(0), BigDecimal.valueOf(200), "PENDING", false, null, request.items());
    }

    @GetMapping("/orders/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return new OrderResponse(1L, LocalDateTime.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(0), BigDecimal.valueOf(200), "PENDING", false, null, Collections.emptyList());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<OrderResponse> orders = Arrays.asList(
                new OrderResponse(1L, LocalDateTime.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(0), BigDecimal.valueOf(200), "PENDING", false, null, Collections.emptyList()),
                new OrderResponse(2L, LocalDateTime.now(), BigDecimal.valueOf(300), BigDecimal.valueOf(0), BigDecimal.valueOf(300), "PENDING", false, null, Collections.emptyList())
        );
        return ResponseEntity.ok(orders);
    }

    record OrderItem(Long productId, String productName, int quantity, BigDecimal price) { }
    record OrderRequest(long customerId, List<OrderItem> items){}
    record OrderResponse(Long id, LocalDateTime orderDate, BigDecimal regularPrice, BigDecimal salePrice, BigDecimal sellingPrice
            ,  String status, Boolean isDeleted, LocalDateTime deletedAt, List<OrderItem> items) { }
}




