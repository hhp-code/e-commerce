package com.ecommerce.application;

import com.ecommerce.config.RedisLockManager;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.interfaces.exception.domain.ProductException;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class ProductFacade {
    private final ProductService productService;
    private final RedisLockManager redisLockManager;

    private final Duration lockTimeout = Duration.ofSeconds(5);
    private final Duration actionTimeout = Duration.ofSeconds(5);
    public ProductFacade(ProductService productService, RedisLockManager redisLockManager) {
        this.productService = productService;
        this.redisLockManager = redisLockManager;
    }
    public Product deductStock(Product product, Integer quantity) {
        String lockKey = "product:" + product.getId();
        try {
            return redisLockManager.executeWithLock(lockKey, lockTimeout,actionTimeout, () -> {
                Product myProduct = productService.getProduct(product.getId());
                myProduct.deductStock(quantity);
                return productService.saveProduct(myProduct);
            });
        } catch (Exception e) {
            throw new ProductException.ServiceException("재고 차감 중 오류 발생");
        }
    }


    public Product chargeStock(Product product, Integer quantity) {
        String lockKey = "product:" + product.getId();
        try {
            return redisLockManager.executeWithLock(lockKey, lockTimeout,actionTimeout,
                    () -> {
                product.chargeStock(quantity);
                return productService.saveProduct(product);
            });
        } catch (Exception e) {
            throw new ProductException.ServiceException("재고 증가 중 오류 발생");
        }
    }
}
