package com.ecommerce.cache;

import com.ecommerce.config.DatabaseCleanUp;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("cleanser")
@AutoConfigureTestDatabase
public class UserCacheTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        User user = new User( "TestUser", BigDecimal.valueOf(1000));
        userService.saveUser(user);
        Product product = new Product( "TestProduct", BigDecimal.valueOf(1000), 10);
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
