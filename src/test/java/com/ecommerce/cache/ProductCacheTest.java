package com.ecommerce.cache;

import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class ProductCacheTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    @Transactional
    public void setUp() {
        productService.saveAndGet(new Product("상품1", BigDecimal.valueOf(1000),10));
    }

    @Test
    public void testProductCaching() {
        Long productId = 1L;

        Product product = productService.getProduct(productId);

        Product cachedProduct = productService.getProduct(productId);

        assertThat(cachedProduct).isEqualTo(product);

        assertThat(cacheManager.getCache("products").get(productId)).isNotNull();
    }


}