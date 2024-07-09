package com.ecommerce.api.order.service;

import com.ecommerce.api.order.service.repository.OrderRepository;
import com.ecommerce.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    public void testGetOrder() {
        Long customerId = 1L;
        Order mockOrder = new Order(); // Order 객체 생성 로직 필요
        when(orderRepository.getById(customerId)).thenReturn(Optional.of(mockOrder));

        Order result = orderService.getOrder(customerId);

        assertNotNull(result);
        assertEquals(mockOrder, result);
        verify(orderRepository).getById(customerId);
    }

    @Test
    public void testGetOrders() {
        OrderCommand.Search searchCommand = new OrderCommand.Search(1L);
        List<Order> mockOrders = List.of(new Order(), new Order());
        when(orderRepository.getOrders(searchCommand)).thenReturn(mockOrders);

        List<Order> result = orderService.getOrders(searchCommand);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository).getOrders(searchCommand);
    }


    @Test
    public void testGetOrderNotFound() {
        Long customerId = 1L;
        when(orderRepository.getById(customerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrder(customerId));
        verify(orderRepository).getById(customerId);
    }
}