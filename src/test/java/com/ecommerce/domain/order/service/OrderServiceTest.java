package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 2L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderServiceOriginal;




    @Nested
    @DisplayName("주문 조회 테스트")
    class GetOrderTests {
        @Test
        @DisplayName("주문 ID로 주문을 조회한다")
        void getOrder_ShouldReturnOrder_WhenOrderExists() {
            Order mockOrder = createMockOrder();
            when(orderRepository.getById(ORDER_ID)).thenReturn(Optional.of(mockOrder));

            Order result = orderServiceOriginal.getOrder(ORDER_ID);

            assertNotNull(result);
            assertEquals(mockOrder, result);
        }

        @Test
        @DisplayName("주문 검색 조건으로 주문 목록을 조회한다")
        void getOrders_ShouldReturnOrderList_WhenSearchConditionProvided() {
            OrderCommand.Search searchCommand = new OrderCommand.Search(USER_ID);
            List<Order> mockOrders = List.of(createMockOrder(), createMockOrder());
            when(orderRepository.getOrders(searchCommand)).thenReturn(mockOrders);

            List<Order> result = orderServiceOriginal.getOrders(searchCommand);

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTests {
        @Test
        @DisplayName("새로운 주문을 생성한다")
        void createOrder_ShouldCreateNewOrder_WhenValidCommandProvided() {
            User mockUser = createMockUser();
            OrderCommand.Create createCommand = new OrderCommand.Create(USER_ID, List.of(createMockCartItem()));
            Order mockOrder = new Order(mockUser, createCommand.items());

            when(userService.getUser(USER_ID)).thenReturn(mockUser);
            when(orderRepository.saveAndGet(any(Order.class))).thenReturn(Optional.of(mockOrder));

            Order result = orderServiceOriginal.createOrder(createCommand);

            assertNotNull(result);
            assertEquals(mockOrder, result);
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
        return new Order(ORDER_ID, createMockUser(), List.of(createMockCartItem()));
    }
}