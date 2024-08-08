package com.ecommerce.api.controller.domain.order;

import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.order.service.OrderQueryService;
import com.ecommerce.interfaces.controller.domain.order.OrderController;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderMapper;
import com.ecommerce.application.usecase.CartUseCase;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderCommandService;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    private static final String API_ORDERS = "/api/orders";
    private static final String API_ORDERS_PAYMENTS = "/api/orders/payments";
    private static final long VALID_USER_ID = 1L;
    private static final long INVALID_USER_ID = -1L;
    private static final long NON_EXISTENT_ORDER_ID = 9999L;
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1000);
    private static final int PRODUCT_STOCK = 100;
    private static final long PRODUCT_ID = 1L;
    private static final long ORDER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderCommandService orderCommandService;

    @MockBean
    private PaymentUseCase paymentUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @Autowired
    private ObjectMapper objectMapper;
    private final Product product = new Product(1L,"product", PRODUCT_PRICE, PRODUCT_STOCK);

    private final Map<Product, Integer> items = Map.of(product, 1);
    private final Map<Long, Integer> create = Map.of(PRODUCT_ID, 1);
    private final OrderCommand.Create request = new OrderCommand.Create(VALID_USER_ID, create);
    private final Order order = new Order( new User(VALID_USER_ID, "test", PRODUCT_PRICE), items);
    private OrderQueryService orderQueryService;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() throws Exception {

        OrderInfo.Summary orderInfo = OrderInfo.Summary.from(order);
        when(paymentUseCase.orderCommandService.createOrder(any(OrderCommand.Create.class), paymentUseCase)).thenReturn(orderInfo);


        mockMvc.perform(post(API_ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_USER_ID));
    }


    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
    void getOrder_NonExistentOrder_ShouldFail() throws Exception {
        when(orderQueryService.getOrder(NON_EXISTENT_ORDER_ID)).thenThrow(new RuntimeException("주문을 찾을 수 없습니다."));

        mockMvc.perform(get(API_ORDERS + "/{orderId}", NON_EXISTENT_ORDER_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("주문 목록 조회 실패 - 잘못된 요청 파라미터")
    void listOrders_InvalidRequest_ShouldFail() throws Exception {
        OrderDto.OrderCreateRequest request = new OrderDto.OrderCreateRequest(INVALID_USER_ID, Map.of());

        mockMvc.perform(get(API_ORDERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("주문자의 ID가 잘못되었습니다."));
    }

    @Test
    @DisplayName("결제 요청 - 결제 성공")
    void payOrder_Success() throws Exception {
        OrderDto.OrderPayRequest request = new OrderDto.OrderPayRequest(ORDER_ID);
        Order order = createSampleOrder();
        OrderInfo.Detail orderInfo = OrderInfo.Detail.from(order);
        when(paymentUseCase.payOrder(OrderMapper.toOrderPay(request))).thenReturn(orderInfo);

        mockMvc.perform(post(API_ORDERS_PAYMENTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_USER_ID));
    }



    @Test
    @DisplayName("주문 상품 추가")
    void addCartItemToOrder_Success() throws Exception {
        OrderCommand.Add request = new OrderCommand.Add(VALID_USER_ID, ORDER_ID, 1);
        Order order = createSampleOrder();
        OrderInfo.Detail orderInfo = OrderInfo.Detail.from(order);

        when(cartUseCase.addItemToOrder(request)).thenReturn(orderInfo);

        mockMvc.perform(patch(API_ORDERS +"/{orderId}"+"/items",order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_USER_ID));
    }

    @Test
    @DisplayName("주문 상품 삭제")
    void deleteCartItemToOrder_Success() throws Exception {
        OrderCommand.Delete request = new OrderCommand.Delete(ORDER_ID,PRODUCT_ID);
        Order order = createSampleOrder();
        OrderInfo.Detail orderInfo = OrderInfo.Detail.from(order);

        when(cartUseCase.deleteItemFromOrder(request)).thenReturn(orderInfo);

        mockMvc.perform(delete(API_ORDERS +"/{orderID}"+ "/items",order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_USER_ID));
    }

    private Order createSampleOrder() {
        User user = new User(VALID_USER_ID, "test", PRODUCT_PRICE);
        Map<Product, Integer> items = new HashMap<>();
        items.put(product, 1);
        Order order = new Order(user, items);
        user.addOrder(order);
        return order;
    }
}