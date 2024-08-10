package com.ecommerce.cache;

import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.order.command.OrderCommandService;
import com.ecommerce.domain.order.query.OrderQueryService;
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
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class OrderEntityCacheTest {
    @Autowired
    private OrderCommandService orderCommandService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentUseCase paymentUseCase;
    @Autowired
    private OrderQueryService orderQueryService;

    @BeforeEach
    void setUp() {
        userService.saveUser(new User(1L, "TestUser", BigDecimal.valueOf(1000)));
        productService.saveAndGet(new Product(1L, "TestProduct", BigDecimal.valueOf(1000), 10));
        Long productId = 1L;
        Map<Long, Integer> items = Map.of(productId, 1);
        OrderCommand.Create createOrderCommand = new OrderCommand.Create(1L, items);
        paymentUseCase.orderCommandService.createOrder(createOrderCommand, paymentUseCase);
    }

    @Test
    public void testOrderCaching() {
        //given
        Long userId = 1L;

        //when
        orderQueryService.getOrder(1L);
        orderQueryService.getOrder(1L);

        //then
        assertThat(cacheManager.getCache("orders").get(userId)).isNotNull();
    }

}
