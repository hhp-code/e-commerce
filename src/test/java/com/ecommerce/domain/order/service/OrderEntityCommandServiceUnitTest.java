package com.ecommerce.domain.order.service;

import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.command.OrderCommandService;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.query.OrderQueryService;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.order.command.OrderCommandRepository;
import com.ecommerce.domain.order.query.OrderQueryRepository;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderEntityCommandServiceUnitTest {

    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = 999L;

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @Mock
    private OrderQueryRepository orderQueryRepository;

    @InjectMocks
    private OrderCommandService orderCommandService;

    PaymentUseCase paymentUseCase;
    @InjectMocks
    private OrderQueryService orderQueryService;


    @Test
    @DisplayName("존재하는 주문 조회 시 성공")
    void getOrder_ExistingOrder_ShouldSucceed() {
        OrderEntity mockOrderEntity = createMockOrder();
        when(orderQueryRepository.getById(VALID_USER_ID)).thenReturn(Optional.of(mockOrderEntity));

        OrderEntity result = orderQueryService.getOrder(VALID_USER_ID);

        assertNotNull(result);
        verify(orderQueryRepository).getById(VALID_USER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 주문 조회 시 예외 발생")
    void getOrder_NonExistentOrder_ShouldThrowException() {
        when(orderQueryRepository.getById(INVALID_USER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderQueryService.getOrder(INVALID_USER_ID));
        verify(orderQueryRepository).getById(INVALID_USER_ID);
    }


    @Test
    @DisplayName("검색 조건에 맞는 주문 목록 반환")
    void getOrders_WithSearchCondition_ShouldReturnOrderList() {
        OrderQuery.GetUserOrders searchCommand = new OrderQuery.GetUserOrders(VALID_USER_ID);
        List<OrderEntity> mockOrderEntities = Arrays.asList(createMockOrder(), createMockOrder());
        when(orderQueryRepository.getOrders(searchCommand.userId())).thenReturn(mockOrderEntities);

        List<OrderEntity> result = orderQueryService.getOrders(searchCommand);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderQueryRepository).getOrders(VALID_USER_ID);
    }



    @Test
    @DisplayName("주문 생성 실패 시 예외 발생")
    void createOrder_Failure_ShouldThrowException() {
        OrderCommand.Create createCommand = new OrderCommand.Create(VALID_USER_ID.intValue(), Map.of());

        assertThrows(RuntimeException.class, () -> paymentUseCase.orderCommandService.createOrder(createCommand, paymentUseCase));
    }

    private OrderEntity createMockOrder() {
        User user = new User(VALID_USER_ID, "test", BigDecimal.ONE);
        return new OrderEntity(user, Map.of(new Product("product", BigDecimal.ONE, 1000), 1));
    }
}