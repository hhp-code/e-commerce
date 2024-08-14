package com.ecommerce.api.controller.usecase;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.OrderFacade;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.OrderInfo;
import com.ecommerce.domain.product.ProductWrite;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.UserWrite;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.*;
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
class CartUseCaseTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartUseCase cartUseCase;
    @Autowired
    private UserService userService;

    @Autowired
    private PaymentUseCase paymentUseCase;

    private UserWrite testUser;
    private ProductWrite testProduct;
    @Autowired
    private OrderFacade orderFacade;

    @BeforeEach
    void setUp(){
        testUser = userService.saveUser(new UserWrite("testUser", BigDecimal.valueOf(1000)));
        testProduct = productService.saveAndGet(new ProductWrite("testProduct", BigDecimal.valueOf(1000), 100));

    }

    @Test
    @DisplayName("기존 주문에 장바구니 아이템을 추가한다")
    void addCartItemToOrder_ShouldAddItemToExistingOrder_WhenValidCommandProvided() {
        //given
        OrderCommand.Add addCommand = new OrderCommand.Add(testUser.getId(), testProduct.getId(), 1);
        OrderItemWrite orderItemWrite = new OrderItemWrite(testProduct, 1);

        OrderCommand.Create createCommand = new OrderCommand.Create(testUser.getId(), List.of(orderItemWrite));
        orderFacade.createOrder(createCommand);

        //when
        OrderInfo.Detail result = cartUseCase.addItemToOrder(addCommand);

        //then
        assertNotNull(result);
        assertEquals(1, result.items().size());
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }
}