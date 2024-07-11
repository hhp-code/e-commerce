package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class PaymentUseCaseTest {
    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 2L;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @Nested
    @DisplayName("주문 결제 테스트")
    class PayOrderTests {

        @Test
        @DisplayName("주문을 결제 처리한다")
        void payOrder_ShouldProcessPayment_WhenValidCommandProvided() {
            Order mockOrder = createMockOrder();
            mockOrder.start();
            OrderCommand.Payment paymentCommand = new OrderCommand.Payment(ORDER_ID, mockOrder.getTotalAmount());

            when(orderService.getOrder(ORDER_ID)).thenReturn(mockOrder);

            Order result = paymentUseCase.payOrder(paymentCommand);

            assertNotNull(result);
            assertEquals(OrderStatus.ORDERED.name(), result.getStatus());
        }
    }
    private User createMockUser() {
        return new User(USER_ID, "testUser", BigDecimal.valueOf(1000));
    }

    private Product createMockProduct() {
        return new Product(1L,"test", BigDecimal.TWO, 1000);
    }

    private OrderItem createMockCartItem() {
        return new OrderItem(createMockProduct(), 1);
    }

    private Order createMockOrder() {
        Order order = new Order(ORDER_ID, createMockUser(), List.of(createMockCartItem()));
        order.start();
        return order;
    }
}