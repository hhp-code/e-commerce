package com.ecommerce.cache;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserCacheTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "TestUser", BigDecimal.valueOf(1000));
        userService.saveUser(user);
        Product product = new Product(1L, "TestProduct", BigDecimal.valueOf(1000), 10);
        productService.saveAndGet(product);
    }


    @Test
    public void testUserCaching() {
        //given
        Long userId = 1L;

        //when
        User user = userService.getUser(userId);
        User cachedUser = userService.getUser(userId);

        //then
        assertThat(cachedUser).isEqualTo(user);
        assertThat(cacheManager.getCache("users").get(userId)).isNotNull();
    }


}
