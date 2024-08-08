package com.ecommerce.interfaces.controller.domain.order;

import com.ecommerce.application.OrderFacade;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderMapper;
import com.ecommerce.application.usecase.CartUseCase;
import com.ecommerce.application.usecase.PaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "order", description = "주문 관련 API")
@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderFacade orderFacade;
    private final CartUseCase cartUseCase;
    private final PaymentUseCase paymentUseCase;

    public OrderController(OrderFacade orderFacade, CartUseCase cartUseCase, PaymentUseCase paymentUseCase) {
        this.orderFacade = orderFacade;
        this.cartUseCase = cartUseCase;
        this.paymentUseCase = paymentUseCase;
    }

    @PostMapping("/orders")
    @Operation(
            summary = "주문 생성",
            description = "새로운 주문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 생성 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderDetailResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public OrderDto.OrderSummaryResponse createOrder(@RequestBody OrderDto.OrderCreateRequest request) {
        request.validate();
        return OrderMapper.toOrderSummaryResponse(orderFacade.createOrder(OrderMapper.toOrder(request)));
    }

    @GetMapping("/orders/{orderId}")
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

    @PatchMapping("/orders/{orderId}/items")
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
    public OrderDto.OrderDetailResponse addCartItemToOrder(
            @Parameter(description = "상품을 추가할 주문의 ID") @PathVariable Long orderId,
            @RequestBody OrderDto.OrderAddItemRequest request
    ) {
        request.validate();
        return OrderMapper.toOrderDetailResponse(cartUseCase.addItemToOrder(OrderMapper.toOrderAddItem(orderId, request)));
    }
    @DeleteMapping("/orders/{orderId}/items")
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
    public OrderDto.OrderDetailResponse deleteCartItemToOrder(
            @Parameter(description = "주문에서 제거할 상품의 ID") @PathVariable Long orderId,
            @RequestBody OrderDto.OrderDeleteItemRequest request
    ) {
        request.validate();
        return OrderMapper.toOrderDetailResponse(
                cartUseCase.deleteItemFromOrder(OrderMapper.toOrderDeleteItem(orderId, request))
        );
    }

    @GetMapping("/orders")
    @Operation(
            summary = "주문 목록 조회",
            description = "주문 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = OrderDto.OrderListResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public OrderDto.OrderListResponse listOrders(@RequestBody OrderDto.OrderListRequest request){
        request.validate();
        return OrderMapper.toOrderListResponse(orderFacade.getOrders(OrderMapper.toGetUserOrders(request)));
    }

    @PostMapping("/orders/payments")
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
    public OrderDto.OrderDetailResponse payOrder(@RequestBody OrderDto.OrderPayRequest request){
        request.validate();
        return OrderMapper.toOrderDetailResponse(paymentUseCase.payOrder(OrderMapper.toOrderPay(request)));
    }
    @PatchMapping("/orders/cancel")
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
    public OrderDto.OrderDetailResponse cancelOrder(@RequestBody OrderDto.OrderCancelRequest request){
        request.validate();
        return OrderMapper.toOrderDetailResponse(
                paymentUseCase.cancelOrder(
                        OrderMapper.toOrderCancel(request)
                )
        );
    }
}