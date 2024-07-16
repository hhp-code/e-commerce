package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserBalanceCommand;
import com.ecommerce.domain.user.service.UserBalanceService;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentUseCaseTest {
    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 2L;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;
    @Mock
    private UserBalanceService userBalanceService;
    @Mock
    private DummyPlatform dummyPlatform;
    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @Test
    @DisplayName("주문을 결제 처리한다")
    void payOrder_ShouldProcessPayment_WhenValidCommandProvided() {
        Order mockOrder = createMockOrder();
        OrderCommand.Payment paymentCommand = new OrderCommand.Payment(USER_ID,ORDER_ID);

        when(orderService.getOrder(ORDER_ID)).thenReturn(mockOrder);
        when(dummyPlatform.send(mockOrder)).thenReturn(true);
        when(orderService.saveAndGet(any(Order.class))).thenReturn(mockOrder);

        Order result = paymentUseCase.payOrder(paymentCommand);

        assertNotNull(result);
        assertEquals(OrderStatus.ORDERED.name(), result.getOrderStatus());
    }


    @Test
    @DisplayName("재고 부족 시 보상 트랜잭션이 정상적으로 실행된다")
    void payOrder_ShouldCompensate_WhenStockIsInsufficient() {
        // Given
        Order mockOrder = createMockOrder();
        OrderCommand.Payment paymentCommand = new OrderCommand.Payment(USER_ID,ORDER_ID);
        when(orderService.getOrder(ORDER_ID)).thenReturn(mockOrder);
        doThrow(new RuntimeException("재고 부족")).when(productService).decreaseStock(any(Product.class), anyInt());
        // When & Then
        assertThrows(RuntimeException.class, () -> paymentUseCase.payOrder(paymentCommand));
        verify(userBalanceService).chargeBalance(any(UserBalanceCommand.Create.class));
        verify(orderService).saveAndGet(any(Order.class));
        verify(productService).increaseStock(any(Product.class), anyInt());

    }

    @Test
    @DisplayName("사용자 잔액 부족 시 보상 트랜잭션이 정상적으로 실행된다")
    void payOrder_ShouldCompensate_WhenUserBalanceIsInsufficient() {
        // Given
        Order mockOrder = createMockOrder();
        OrderCommand.Payment paymentCommand = new OrderCommand.Payment(USER_ID,ORDER_ID);
        when(orderService.getOrder(ORDER_ID)).thenReturn(mockOrder);
        doThrow(new RuntimeException("잔액 부족")).when(userBalanceService).decreaseBalance(any(User.class), any(BigDecimal.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> paymentUseCase.payOrder(paymentCommand));
        verify(productService).increaseStock(any(Product.class), anyInt());
        verify(orderService).saveAndGet(any(Order.class));
    }

    private User createMockUser() {
        return new User(USER_ID, "testUser", BigDecimal.valueOf(1000));
    }

    private Product createMockProduct() {
        return new Product(1L, "test", BigDecimal.TWO, 1000);
    }

    private OrderItem createMockCartItem() {
        return new OrderItem(createMockProduct(), 1);
    }

    private Order createMockOrder() {
        return new Order(ORDER_ID, createMockUser(), List.of(createMockCartItem()));
    }
}