package com.ecommerce.api.order.controller;
import com.ecommerce.api.order.controller.dto.OrderDto;
import com.ecommerce.api.order.controller.dto.OrderMapper;
import com.ecommerce.api.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "order", description = "주문 관련 API")
@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/orders")
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public OrderDto.OrderResponse createOrder(@RequestBody OrderDto.OrderCreateRequest request) {
        request.validate();
        return OrderMapper.toOrderResponse(
                orderService.createOrder(OrderMapper.toOrder(request))
        );
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "주문 조회", description = "주문을 조회합니다.")
    public OrderDto.OrderResponse getOrder(@PathVariable Long orderId) {
        if(orderId < 0){
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }
        return OrderMapper.toOrderResponse(
                orderService.getOrder(orderId)
        );

    }
    @PatchMapping("/orders/{orderId}/items")
    @Operation(summary = "주문 상품 추가", description = "주문에 상품을 추가합니다.")
    public OrderDto.OrderResponse addCartItemToOrder(@PathVariable Long orderId, @RequestBody OrderDto.OrderAddItemRequest request) {
        request.validate();
        return OrderMapper.toOrderResponse(
                orderService.addCartItemToOrder(OrderMapper.toOrderAddItem(orderId, request))
        );
    }

    @GetMapping("/orders")
    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회합니다.")
    public OrderDto.OrderListResponse listOrders(@RequestBody OrderDto.OrderListRequest request){
        request.validate();
        return OrderMapper.toOrderListResponse(
                orderService.getOrders(OrderMapper.toSearch(request))
        );
    }

    @PostMapping("/orders/payments")
    @Operation(summary = "주문 결제", description = "주문을 결제합니다.")
    public OrderDto.OrderResponse payOrder(@RequestBody OrderDto.OrderPayRequest request){
        request.validate();
        return OrderMapper.toOrderResponse(
                orderService.payOrder(OrderMapper.toOrderPay(request))
        );
    }

}




