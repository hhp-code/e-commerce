package com.ecommerce.api.order.service;

import com.ecommerce.api.domain.*;
import com.ecommerce.api.order.service.repository.OrderRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.product.service.repository.ProductRepository;
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
    private static final Long PRODUCT_ID = 3L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("주문 조회 테스트")
    class GetOrderTests {
        @Test
        @DisplayName("주문 ID로 주문을 조회한다")
        void getOrder_ShouldReturnOrder_WhenOrderExists() {
            Order mockOrder = createMockOrder();
            when(orderRepository.getById(ORDER_ID)).thenReturn(Optional.of(mockOrder));

            Order result = orderService.getOrder(ORDER_ID);

            assertNotNull(result);
            assertEquals(mockOrder, result);
        }

        @Test
        @DisplayName("주문 검색 조건으로 주문 목록을 조회한다")
        void getOrders_ShouldReturnOrderList_WhenSearchConditionProvided() {
            OrderCommand.Search searchCommand = new OrderCommand.Search(USER_ID);
            List<Order> mockOrders = List.of(createMockOrder(), createMockOrder());
            when(orderRepository.getOrders(searchCommand)).thenReturn(mockOrders);

            List<Order> result = orderService.getOrders(searchCommand);

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

            when(userRepository.getById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(orderRepository.saveAndGet(any(Order.class))).thenReturn(Optional.of(mockOrder));

            Order result = orderService.createOrder(createCommand);

            assertNotNull(result);
            assertEquals(mockOrder, result);
        }
    }

    @Nested
    @DisplayName("장바구니 아이템 추가 테스트")
    class AddCartItemToOrderTests {
        @Test
        @DisplayName("기존 주문에 장바구니 아이템을 추가한다")
        void addCartItemToOrder_ShouldAddItemToExistingOrder_WhenValidCommandProvided() {
            User mockUser = createMockUser();
            Product mockProduct = createMockProduct();
            Order mockOrder = createMockOrder();
            OrderCommand.Add addCommand = new OrderCommand.Add(USER_ID, PRODUCT_ID, 1);

            when(userRepository.getById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(orderRepository.findByUserIdAndStatus(USER_ID, OrderStatus.PREPARED)).thenReturn(Optional.of(mockOrder));
            when(productRepository.getProduct(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
            when(orderRepository.saveAndGet(any(Order.class))).thenReturn(Optional.of(mockOrder));

            Order result = orderService.addCartItemToOrder(addCommand);

            assertNotNull(result);
            assertEquals(mockOrder, result);
        }
    }

    @Nested
    @DisplayName("주문 결제 테스트")
    class PayOrderTests {
        @Test
        @DisplayName("주문을 결제 처리한다")
        void payOrder_ShouldProcessPayment_WhenValidCommandProvided() {
            User mockUser = createMockUser();
            Order mockOrder = createMockOrder();
            mockOrder.start();
            OrderCommand.Payment paymentCommand = new OrderCommand.Payment(ORDER_ID, mockOrder.getTotalAmount());

            when(userRepository.getById(ORDER_ID)).thenReturn(Optional.of(mockUser));
            when(orderRepository.findByUserIdAndStatus(mockUser.getId(), OrderStatus.PREPARED)).thenReturn(Optional.of(mockOrder));
            when(orderRepository.saveAndGet(any(Order.class))).thenReturn(Optional.of(mockOrder));
            when(productRepository.getProduct(any())).thenReturn(Optional.of(createMockProduct()));
            when(productRepository.decreaseStock(any(), anyInt())).thenReturn(1);

            Order result = orderService.payOrder(paymentCommand);

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

    private CartItem createMockCartItem() {
        return new CartItem(createMockProduct(), 1);
    }

    private Order createMockOrder() {
        Order order = new Order(ORDER_ID, createMockUser(), List.of(createMockCartItem()));
        order.start();
        return order;
    }
}