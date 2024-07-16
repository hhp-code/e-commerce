package com.ecommerce.api.controller.domain.order;

import com.ecommerce.api.controller.domain.order.dto.OrderDto;
import com.ecommerce.api.controller.domain.order.dto.OrderMapper;
import com.ecommerce.api.controller.usecase.CartUseCase;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.api.controller.usecase.PaymentUseCase;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private static final String API_ORDERS = "/api/orders";
    private static final String API_ORDERS_PAYMENTS = "/api/orders/payments";
    private static final long VALID_USER_ID = 1L;
    private static final long INVALID_USER_ID = -1L;
    private static final long NON_EXISTENT_ORDER_ID = 9999L;
    private static final int MAX_ORDER_QUANTITY = 10;
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1000);
    private static final int PRODUCT_STOCK = 100;
    private static final long ORDER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderServiceOriginal;

    @MockBean
    private PaymentUseCase paymentUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTests {
        @Test
        @DisplayName("주문 생성 실패 - 최대 주문 수량 초과")
        void createOrder_ExceedMaxQuantity_ShouldFail() throws Exception {
            OrderDto.OrderCreateRequest request = createOrderRequestWithExceededQuantity();

            mockMvc.perform(post(API_ORDERS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("주문 수량은 최대 10개까지 가능합니다."));
        }
    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class GetOrderTests {
        @Test
        @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
        void getOrder_NonExistentOrder_ShouldFail() throws Exception {
            when(orderServiceOriginal.getOrder(NON_EXISTENT_ORDER_ID)).thenThrow(new RuntimeException("주문을 찾을 수 없습니다."));

            mockMvc.perform(get(API_ORDERS + "/{orderId}", NON_EXISTENT_ORDER_ID))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("주문 목록 조회 테스트")
    class ListOrdersTests {
        @Test
        @DisplayName("주문 목록 조회 실패 - 잘못된 요청 파라미터")
        void listOrders_InvalidRequest_ShouldFail() throws Exception {
            OrderDto.OrderCreateRequest request = new OrderDto.OrderCreateRequest(INVALID_USER_ID, List.of());

            mockMvc.perform(get(API_ORDERS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("주문자의 ID가 잘못되었습니다."));
        }
    }

    @Nested
    @DisplayName("결제 요청 테스트")
    class PayOrderTests {
        @Test
        @DisplayName("결제 요청 - 결제 성공")
        void payOrder_Success() throws Exception {
            OrderDto.OrderPayRequest request = new OrderDto.OrderPayRequest(VALID_USER_ID, ORDER_ID);
            Order order = createSampleOrder();

            when(paymentUseCase.payOrder(OrderMapper.toOrderPay(request))).thenReturn(order);

            mockMvc.perform(post(API_ORDERS_PAYMENTS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(VALID_USER_ID));
        }
    }

    private OrderDto.OrderCreateRequest createOrderRequestWithExceededQuantity() {
        List<OrderItem> items = new ArrayList<>();
        Product product = new Product("test", PRODUCT_PRICE, PRODUCT_STOCK);
        for (int i = 0; i < MAX_ORDER_QUANTITY + 1; i++) {
            items.add(new OrderItem(product, 20));
        }
        return new OrderDto.OrderCreateRequest(VALID_USER_ID, items);
    }

    private Order createSampleOrder() {
        User user = new User(VALID_USER_ID, "test", PRODUCT_PRICE);
        List<OrderItem> items = List.of(new OrderItem(new Product("test", PRODUCT_PRICE, PRODUCT_STOCK), 1));
        Order order = new Order(VALID_USER_ID, user, items);
        user.addOrder(order);
        return order;
    }
}