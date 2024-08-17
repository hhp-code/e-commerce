package com.ecommerce.domain.order.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.OrderFacade;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.checkerframework.checker.units.qual.A;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class OrderCommandServiceTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private OrderService orderQueryService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    private static final Long ORDER_ID = 1L;



    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    private Order testOrder;
    private User testUser;
    private Product testProduct;
    @Autowired
    private PaymentUseCase paymentUseCase;
    @Autowired
    private OrderFacade orderFacade;

    @BeforeEach
    void setup() {
        User user = new User("testUser", BigDecimal.valueOf(1000));
        Product product = new Product( "test", BigDecimal.TWO, 1000);
        testUser = userService.saveUser(user);
        testProduct = productService.saveAndGet(product);
        Map<Product,Integer> orderItem = Map.of(testProduct, 1);
        Order order = new Order(testUser, orderItem);
        testOrder = orderQueryService.saveOrder(order);
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회한다")
    void getOrder_ShouldReturnOrder_WhenOrderExists() {

        Order result = orderQueryService.getOrder(testOrder.getId());

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
    }

    @Test
    @DisplayName("주문 검색 조건으로 주문 목록을 조회한다")
    void getOrders_ShouldReturnOrderList_WhenSearchConditionProvided() {
        OrderQuery.GetUserOrders searchCommand = new OrderQuery.GetUserOrders(testUser.getId());

        List<Order> result = orderQueryService.getOrders(searchCommand);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("새로운 주문을 생성한다")
    void createOrder_ShouldCreateNewOrder_WhenValidCommandProvided() {
        OrderCommand.Create createCommand = new OrderCommand.Create(testUser.getId(), Map.of(testProduct.getId(), 1));

        OrderInfo.Summary result = orderFacade.createOrder(createCommand);

        assertNotNull(result);
        assertEquals("PREPARED", result.status());
    }



}