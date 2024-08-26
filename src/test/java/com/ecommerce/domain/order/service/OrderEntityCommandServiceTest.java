package com.ecommerce.domain.order.service;

import com.ecommerce.config.DatabaseCleanUp;
import com.ecommerce.application.OrderFacade;
import com.ecommerce.domain.order.*;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class OrderEntityCommandServiceTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderFacade orderFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    private static final Long ORDER_ID = 1L;


    @Autowired
    private OrderService orderCommandService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    private OrderWrite testOrderWrite;
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setup() {
        User user = new User("testUser", BigDecimal.valueOf(1000));
        Product product = new Product( "test", BigDecimal.TWO, 1000);
        testUser = userService.saveUser(user);
        testProduct = productService.saveAndGet(product);
        OrderItemWrite orderItem = new OrderItemWrite(testProduct, 1);
        OrderWrite orderWrite = new OrderWrite(testUser, List.of(orderItem));
        testOrderWrite = orderCommandService.saveOrder(orderWrite);
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회한다")
    void getOrder_ShouldReturnOrder_WhenOrderExists() {

        OrderWrite result = orderService.getOrder(testOrderWrite.getId());

        assertNotNull(result);
        assertEquals(testOrderWrite.getId(), result.getId());
    }

    @Test
    @DisplayName("주문 검색 조건으로 주문 목록을 조회한다")
    void getOrders_ShouldReturnOrderList_WhenSearchConditionProvided() {
        OrderQuery.GetUserOrders searchCommand = new OrderQuery.GetUserOrders(testUser.getId());

        List<OrderRead> result = orderService.getOrders(searchCommand);

        assertNotNull(result);
        assertEquals(1, result.size());
    }





}