package com.ecommerce.domain.order.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.query.OrderQuery;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class OrderEntityCommandServiceTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private OrderService orderService;

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
    @Autowired
    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setup() {
        User user = new User("testUser", BigDecimal.valueOf(1000));
        Product product = new Product( "test", BigDecimal.TWO, 1000);
        testUser = userService.saveUser(user);
        testProduct = productService.saveAndGet(product);
        OrderItemWrite orderItem = new OrderItemWrite(testProduct, 1);
        OrderEntity orderEntity = new OrderEntity(testUser, List.of(orderItem));
        OrderWrite orderWrite = OrderDomainMapper.toWriteModel(orderEntity);
        testOrderWrite = orderCommandService.saveOrder(orderWrite);
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회한다")
    void getOrder_ShouldReturnOrder_WhenOrderExists() {

        OrderEntity result = orderService.getOrder(testOrderWrite.getId());

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


    @Test
    @DisplayName("새로운 주문을 생성한다")
    void createOrder_ShouldCreateNewOrder_WhenValidCommandProvided() {
        OrderCommand.Create createCommand = new OrderCommand.Create(testUser.getId(), Map.of(testProduct.getId(), 1));

        OrderInfo.Summary result = paymentUseCase.orderCommandService.createOrder(createCommand, paymentUseCase);

        assertNotNull(result);
        assertEquals("PREPARED", result.status());
    }



}