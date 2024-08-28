package com.ecommerce.cache;

import com.ecommerce.application.OrderFacade;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class OrderCacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderQueryService;
    @Autowired
    private OrderFacade orderFacade;

    @BeforeEach
    void setUp() {
        userService.saveUser(new User(1L, "TestUser", BigDecimal.valueOf(1000)));
        productService.saveAndGet(new Product(1L, "TestProduct", BigDecimal.valueOf(1000), 10));
        Long productId = 1L;
        Map<Long, Integer> items = Map.of(productId, 1);
        OrderCommand.Create createOrderCommand = new OrderCommand.Create(1L, items);
        orderFacade.createOrder(createOrderCommand);
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

    @Test
    @DirtiesContext
    public void testOrderCachingPerformance() {
        Long orderId = 1L;

        // 첫 번째 조회 (캐시되지 않은 상태)
        Instant start = Instant.now();
        for(int i = 0; i < 1000; i++) {
            orderQueryService.getOrder(orderId);
        }
        Instant end = Instant.now();
        long uncachedDuration = Duration.between(start, end).toMillis();

        // 두 번째 조회 (캐시된 상태)
        start = Instant.now();
        for(int i = 0; i < 1000; i++) {
            orderQueryService.getOrder(orderId);
        }
        end = Instant.now();
        long cachedDuration = Duration.between(start, end).toMillis();

        // 세 번째 조회 (캐시 변경 상태, 조회 9번과 변경 1번)
        start = Instant.now();
        for(int i = 0; i < 100; i++) {
            Order order = orderQueryService.getOrder(orderId);
            for(int j = 0; j < 9; j++) {
                order = orderQueryService.getOrder(orderId);
            }
            order.addItem(productService, 1L, 1);
            orderQueryService.saveOrder(order);
        }
        end = Instant.now();
        long evictedDuration = Duration.between(start, end).toMillis();

        // 성능 향상 검증
        assertThat(cachedDuration).isLessThan(uncachedDuration);

        // 구체적인 성능 향상 비율 계산 (예: 50% 이상 빨라졌는지)
        double improvementRatio = (uncachedDuration - cachedDuration) / (double) uncachedDuration;
        assertThat(improvementRatio).isGreaterThan(0.5); // 50% 이상 성능 향상 기대

        System.out.println("Uncached query time: " + uncachedDuration + "ms");
        System.out.println("Cached query time: " + cachedDuration + "ms");
        System.out.println("Evicted query time: " + evictedDuration + "ms");
        System.out.println("Performance improvement: " + (improvementRatio * 100) + "%");

    }

}
