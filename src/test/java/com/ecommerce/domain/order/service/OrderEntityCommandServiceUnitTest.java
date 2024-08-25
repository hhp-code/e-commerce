package com.ecommerce.domain.order.service;

import com.ecommerce.application.OrderFacade;
import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.order.command.OrderCommandRepository;
import com.ecommerce.domain.order.query.OrderQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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
    private OrderService orderCommandService;

    @InjectMocks
    private OrderService orderService;

    private OrderFacade orderFacade;


    @Test
    @DisplayName("존재하는 주문 조회 시 성공")
    void getOrder_ExistingOrder_ShouldSucceed() {
        OrderWrite mockOrderEntity = createMockOrder();

        OrderWrite result = orderService.getOrder(VALID_USER_ID);

        assertNotNull(result);
        verify(orderQueryRepository).getById(VALID_USER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 주문 조회 시 예외 발생")
    void getOrder_NonExistentOrder_ShouldThrowException() {
        when(orderQueryRepository.getById(INVALID_USER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrder(INVALID_USER_ID));
        verify(orderQueryRepository).getById(INVALID_USER_ID);
    }


    @Test
    @DisplayName("검색 조건에 맞는 주문 목록 반환")
    void getOrders_WithSearchCondition_ShouldReturnOrderList() {
        OrderQuery.GetUserOrders searchCommand = new OrderQuery.GetUserOrders(VALID_USER_ID);
        List<OrderWrite> mockOrderEntities = Arrays.asList(createMockOrder(), createMockOrder());

        List<OrderRead> result = orderService.getOrders(searchCommand);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderQueryRepository).getOrders(VALID_USER_ID);
    }



    private OrderWrite createMockOrder() {
        User user = new User( "test", BigDecimal.ONE);
        OrderItemWrite orderItemEntity = new OrderItemWrite(new Product("product", BigDecimal.ONE, 1000), 1);
        return new OrderWrite(user, List.of(orderItemEntity));
    }
}