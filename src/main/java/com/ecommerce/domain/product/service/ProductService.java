package com.ecommerce.domain.product.service;


import com.ecommerce.api.exception.domain.ProductException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.product.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Component
public class ProductService {

    private final ProductRepository productRepository;

    private final QuantumLockManager quantumLockManager;

    public ProductService(ProductRepository productRepository, QuantumLockManager quantumLockManager) {
        this.productRepository = productRepository;
        this.quantumLockManager = quantumLockManager;
    }

    public Product getProduct(Long productId) {
        return productRepository.getProduct(productId).orElseThrow(
                () -> new ProductException.ServiceException("상품을 찾을 수 없습니다.")
        );
    }

    @Transactional
    public List<Product> getPopularProducts() {
        return productRepository.getPopularProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    public void deductStock(Product product, Integer quantity) {
        String lockKey = "product:" + product.getId();
        Duration timeout = Duration.ofSeconds(5);
        try {
            quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                product.deductStock(quantity);
                return productRepository.save(product).orElseThrow(
                        () -> new ProductException.ServiceException("재고 차감에 실패했습니다.")
                );
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
                return productRepository.save(product).orElseThrow(
                        () -> new ProductException.ServiceException("재고 차감에 실패했습니다.")
                );
            });
        } catch (Exception e) {
            throw new ProductException.ServiceException("재고 차감 중 오류 발생");
        }
    }

    @Transactional
    public Product saveAndGet(Product testProduct) {
        return productRepository.save(testProduct).orElseThrow(
                () -> new ProductException.ServiceException("상품 저장에 실패했습니다.")
        );
    }
}
