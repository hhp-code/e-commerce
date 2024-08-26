package com.ecommerce.interfaces.controller.domain.order;

import com.ecommerce.application.CommandHandler;
import com.ecommerce.application.OrderFacade;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "order", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderFacade orderFacade;
    private final CommandHandler commandHandler;

    public OrderController(OrderFacade orderFacade, CommandHandler commandHandler) {
        this.orderFacade = orderFacade;
        this.commandHandler = commandHandler;
    }

    @PostMapping
    @Operation(
            summary = "주문 생성",
            description = "새로운 주문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 생성 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public ResponseEntity<String> createOrder(@RequestBody OrderDto.OrderCreateRequest request) {
        request.validate();
        commandHandler.handle(OrderMapper.toOrder(request));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "주문 조회",
            description = "주문 ID로 특정 주문을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 조회 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
            }
    )
    public OrderDto.OrderDetailResponse getOrder(
            @Parameter(description = "조회할 주문의 ID") @PathVariable Long orderId
    ) {
        if(orderId < 0){
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }
        return OrderMapper.toOrderDetailResponse(
                orderFacade.getOrder(
                        OrderMapper.toGetOrder(orderId)
                )
        );
    }

    @PatchMapping("/{orderId}/items")
    @Operation(
            summary = "주문 상품 추가",
            description = "기존 주문에 새로운 상품을 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 추가 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
            }
    )
    public ResponseEntity<String> addCartItemToOrder(
            @Parameter(description = "상품을 추가할 주문의 ID") @PathVariable Long orderId,
            @RequestBody OrderDto.OrderAddItemRequest request
    ) {
        request.validate();
        commandHandler.handle(OrderMapper.toOrderAddItem(orderId, request));
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{orderId}/items")
    @Operation(
            summary = "주문 상품 추가",
            description = "기존 주문에 새로운 상품을 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 추가 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
            }
    )
    public ResponseEntity<String> deleteCartItemToOrder(
            @Parameter(description = "주문에서 제거할 상품의 ID") @PathVariable Long orderId,
            @RequestBody OrderDto.OrderDeleteItemRequest request
    ) {
        request.validate();
        commandHandler.handle(OrderMapper.toOrderDeleteItem(orderId, request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/list")
    @Operation(
            summary = "주문 목록 조회",
            description = "주문 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderListResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public OrderDto.OrderListResponse listOrders(@RequestBody OrderDto.OrderListRequest request) {
        request.validate();
        return OrderMapper.toOrderListResponse(orderFacade.getOrders(OrderMapper.toGetUserOrders(request)));
    }

    @PostMapping("/payments")
    @Operation(
            summary = "주문 결제",
            description = "주문을 결제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
            }
    )
    public ResponseEntity<String> payOrder(@RequestBody OrderDto.OrderPayRequest request){
        request.validate();
        commandHandler.handle(OrderMapper.toOrderPay(request));
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/cancel")
    @Operation(
            summary = "주문 취소",
            description = "주문을 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "취소 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
            }
    )
    public ResponseEntity<String> cancelOrder(@RequestBody OrderDto.OrderCancelRequest request){
        request.validate();
        commandHandler.handle(OrderMapper.toOrderCancel(request));
        return ResponseEntity.ok().build();
    }
}