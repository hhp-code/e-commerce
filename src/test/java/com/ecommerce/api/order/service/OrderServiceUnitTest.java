package com.ecommerce.api.order.service;

import com.ecommerce.api.order.service.repository.OrderRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(userRepository, orderRepository);
    }

    @Test
    void getOrder_존재하는주문_성공() {
        // Given
        Long customerId = 1L;
        Order mockOrder = new Order(); // Order 객체 생성 필요
        when(orderRepository.getById(customerId)).thenReturn(Optional.of(mockOrder));

        // When
        Order result = orderService.getOrder(customerId);

        // Then
        assertNotNull(result);
        verify(orderRepository).getById(customerId);
    }

    @Test
    void getOrder_존재하지않는주문_예외발생() {
        // Given
        Long customerId = 1L;
        when(orderRepository.getById(customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.getOrder(customerId));
        verify(orderRepository).getById(customerId);
    }

    @Test
    void getOrders_검색조건에맞는주문목록반환() {
        // Given
        long userId = 1L;
        OrderCommand.Search searchCommand = new OrderCommand.Search(userId);
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.getOrders(searchCommand)).thenReturn(mockOrders);

        // When
        List<Order> result = orderService.getOrders(searchCommand);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository).getOrders(searchCommand);
    }

    @Test
    void createOrder_주문생성성공() {
        // Given
        Order mockOrder = new Order();
        OrderCommand.Create createCommand = new OrderCommand.Create(1, List.of());
        when(orderRepository.saveAndGet(mockOrder)).thenReturn(Optional.of(mockOrder));

        // When
        Order result = orderService.createOrder(createCommand);

        // Then
        assertNotNull(result);
        verify(orderRepository).saveAndGet(mockOrder);
    }

    @Test
    void createOrder_주문생성실패_예외발생() {
        // Given
        Order mockOrder = new Order();
        OrderCommand.Create createCommand = new OrderCommand.Create(1,List.of());
        when(orderRepository.saveAndGet(mockOrder)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrder(createCommand));
        verify(orderRepository).saveAndGet(mockOrder);
    }
}