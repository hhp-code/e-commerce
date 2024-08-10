package com.ecommerce.api.controller.usecase;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.usecase.CartUseCase;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

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

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp(){
        testUser = userService.saveUser(new User("testUser", BigDecimal.valueOf(1000)));
        testProduct = productService.saveProduct(new Product("testProduct", BigDecimal.valueOf(1000), 100));

    }

    @Test
    @DisplayName("기존 주문에 장바구니 아이템을 추가한다")
    void addCartItemToOrder_ShouldAddItemToExistingOrder_WhenValidCommandProvided() {
        //given
        OrderCommand.Add addCommand = new OrderCommand.Add(testUser.getId(), testProduct.getId(), 1);
        OrderCommand.Create createCommand = new OrderCommand.Create(testUser.getId(), Map.of(testProduct.getId(), 1));
        paymentUseCase.orderCommandService.createOrder(createCommand, paymentUseCase);

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