package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class PopularProductCaseTest {
    private final Long USER_ID= 1L;
    private final Long ORDER_ID= 1L;
    @Autowired
    private PopularProductCase popularProductCase;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("인기상품을 조회한다 - 6건이상의 테스트 데이터가 있을때 5건을 조회하는가?")
    void getPopularProducts_ShouldReturnProductList_WhenMoreThanSixOrdersExist() {
        for (int i = 0; i < 6; i++) {
            User user = new User(USER_ID + i, "testUser", BigDecimal.valueOf(1000));
            userRepository.save(user);
            Product product = new Product(1L + i, "test", BigDecimal.TWO, 1000);
            productRepository.save(product);
            Map<Product,Integer> orderItem = Map.of(product, 1);
            Order order = new Order(ORDER_ID + i, user, orderItem);
            order.finish();
            orderRepository.saveAndGet(order);
        }

        List<Product> result = popularProductCase.getPopularProducts();

        assertNotNull(result);
        assertEquals(5, result.size());
    }
    @Test
    @DisplayName("인기상품을 조회한다 - 5건 이하일때 모든 데이터를 조회하는가?")
    void getPopularProducts_ShouldReturnProductList_WhenFiveOrdersExist() {
        Product product1 = new Product(1L, "test1", BigDecimal.TWO, 1000);
        Product product2 = new Product(2L, "test2", BigDecimal.TWO, 1000);
        Product product3 = new Product(3L, "test3", BigDecimal.TWO, 1000);
        productRepository.saveAll(Arrays.asList(product1, product2, product3));

        User user = new User(USER_ID, "testUser", BigDecimal.valueOf(1000));
        userRepository.save(user);

        Order order1 = new Order(ORDER_ID, user, Map.of(product1, 3, product2, 2, product3, 1));
        order1.finish();
        orderRepository.saveAndGet(order1);
        List<Product> result = popularProductCase.getPopularProducts();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("test1", result.get(0).getName());
        assertEquals("test2", result.get(1).getName());
        assertEquals("test3", result.get(2).getName());
    }
    @Test
    @DisplayName("인기상품 조회 - 같은 판매량일 경우? 5건만 조회되는가?")
    void getPopularProducts_ShouldReturnProductList_WhenSameSellingAmount() {
        Product product1 = new Product(1L, "test1", BigDecimal.TWO, 1000);
        Product product2 = new Product(2L, "test2", BigDecimal.TWO, 1000);
        Product product3 = new Product(3L, "test3", BigDecimal.TWO, 1000);
        Product product4 = new Product(4L, "test4", BigDecimal.TWO, 1000);
        Product product5 = new Product(5L, "test5", BigDecimal.TWO, 1000);
        Product product6 = new Product(6L, "test6", BigDecimal.TWO, 1000);
        productRepository.saveAll(Arrays.asList(product1, product2, product3, product4, product5, product6));

        User user = new User(USER_ID, "testUser", BigDecimal.valueOf(1000));
        userRepository.save(user);

        Order order1 = new Order(ORDER_ID, user, Map.of(product1, 1,
                product2, 1, product3, 1, product4, 1, product5, 1, product6, 1));
        order1.finish();
        orderRepository.saveAndGet(order1);
        List<Product> result = popularProductCase.getPopularProducts();

        assertNotNull(result);
        assertEquals(5, result.size());
    }

}