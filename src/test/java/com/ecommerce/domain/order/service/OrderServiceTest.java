package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 1L;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        User user = new User(USER_ID, "testUser", BigDecimal.valueOf(1000));
        userRepository.save(user);
        Product product = new Product(1L, "test", BigDecimal.TWO, 1000);
        productRepository.save(product);
        Map<Product,Integer> orderItem = Map.of(product, 1);
        Order order = new Order(ORDER_ID, user, orderItem);
        orderRepository.saveAndGet(order);
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회한다")
    void getOrder_ShouldReturnOrder_WhenOrderExists() {

        Order result = orderService.getOrder(ORDER_ID);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
    }

    @Test
    @DisplayName("주문 검색 조건으로 주문 목록을 조회한다")
    void getOrders_ShouldReturnOrderList_WhenSearchConditionProvided() {
        OrderCommand.Search searchCommand = new OrderCommand.Search(USER_ID);

        List<Order> result = orderService.getOrders(searchCommand);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("새로운 주문을 생성한다")
    void createOrder_ShouldCreateNewOrder_WhenValidCommandProvided() {

        OrderCommand.Create createCommand = new OrderCommand.Create(USER_ID, Map.of(1L, 1));


        Order result = orderService.createOrder(createCommand);

        assertNotNull(result);
        assertEquals(1, result.getOrderItems().size());
    }



}