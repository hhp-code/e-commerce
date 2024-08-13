package com.ecommerce.cache;

import com.ecommerce.application.OrderFacade;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class OrderEntityCacheTest {
    @Autowired
    private OrderService orderCommandService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentUseCase paymentUseCase;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderFacade orderFacade;

    @BeforeEach
    void setUp() {
        userService.saveUser(new User(1L, "TestUser", BigDecimal.valueOf(1000)));
        productService.saveAndGet(new Product(1L, "TestProduct", BigDecimal.valueOf(1000), 10));
        Long productId = 1L;
        Product product = productService.getProduct(productId);
        List<OrderItemWrite> orderItems = List.of(new OrderItemWrite(product, 1));
        OrderCommand.Create createOrderCommand = new OrderCommand.Create(1L, orderItems);
        orderFacade.createOrder(createOrderCommand);
    }

    @Test
    public void testOrderCaching() {
        //given
        Long userId = 1L;

        //when
        orderService.getOrder(1L);
        orderService.getOrder(1L);

        //then
        assertThat(cacheManager.getCache("orders").get(userId)).isNotNull();
    }

}
