package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = 999L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private OrderService orderServiceOriginal;

    @Nested
    @DisplayName("getOrder 메소드 테스트")
    class GetOrderTests {

        @Test
        @DisplayName("존재하는 주문 조회 시 성공")
        void getOrder_ExistingOrder_ShouldSucceed() {
            Order mockOrder = createMockOrder();
            when(orderRepository.getById(VALID_USER_ID)).thenReturn(Optional.of(mockOrder));

            Order result = orderServiceOriginal.getOrder(VALID_USER_ID);

            assertNotNull(result);
            verify(orderRepository).getById(VALID_USER_ID);
        }

        @Test
        @DisplayName("존재하지 않는 주문 조회 시 예외 발생")
        void getOrder_NonExistentOrder_ShouldThrowException() {
            when(orderRepository.getById(INVALID_USER_ID)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> orderServiceOriginal.getOrder(INVALID_USER_ID));
            verify(orderRepository).getById(INVALID_USER_ID);
        }
    }

    @Nested
    @DisplayName("getOrders 메소드 테스트")
    class GetOrdersTests {

        @Test
        @DisplayName("검색 조건에 맞는 주문 목록 반환")
        void getOrders_WithSearchCondition_ShouldReturnOrderList() {
            OrderCommand.Search searchCommand = new OrderCommand.Search(VALID_USER_ID);
            List<Order> mockOrders = Arrays.asList(createMockOrder(), createMockOrder());
            when(orderRepository.getOrders(searchCommand)).thenReturn(mockOrders);

            List<Order> result = orderServiceOriginal.getOrders(searchCommand);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(orderRepository).getOrders(searchCommand);
        }
    }

    @Nested
    @DisplayName("createOrder 메소드 테스트")
    class CreateOrderTests {

        @Test
        @DisplayName("주문 생성 실패 시 예외 발생")
        void createOrder_Failure_ShouldThrowException() {
            OrderCommand.Create createCommand = new OrderCommand.Create(VALID_USER_ID.intValue(), List.of());

            assertThrows(RuntimeException.class, () -> orderServiceOriginal.createOrder(createCommand));
        }
    }

    private Order createMockOrder() {
        return new Order(); // 필요한 경우 Order 객체에 더 많은 정보를 설정할 수 있습니다.
    }
}