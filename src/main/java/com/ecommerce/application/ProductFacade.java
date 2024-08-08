package com.ecommerce.application;

import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.interfaces.exception.domain.ProductException;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class ProductFacade {
    private final ProductService productService;
    private final QuantumLockManager quantumLockManager;
    public ProductFacade(ProductService productService, QuantumLockManager quantumLockManager) {
        this.productService = productService;
        this.quantumLockManager = quantumLockManager;
    }
    public Product deductStock(Product product, Integer quantity) {
        String lockKey = "product:" + product.getId();
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                Product myProduct = productService.getProduct(product.getId());
                myProduct.deductStock(quantity);
                return productService.saveProduct(myProduct);
            });
        } catch (Exception e) {
            throw new ProductException.ServiceException("재고 차감 중 오류 발생");
        }
    }


    public void chargeStock(Product product, Integer quantity) {
        String lockKey = "product:" + product.getId();
        Duration timeout = Duration.ofSeconds(5);
        try {
            quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                product.chargeStock(quantity);
                return productService.saveProduct(product);
            });
        } catch (Exception e) {
            throw new ProductException.ServiceException("재고 증가 중 오류 발생");
        }
    }
}
