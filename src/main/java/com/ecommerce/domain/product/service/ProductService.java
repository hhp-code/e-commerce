package com.ecommerce.domain.product.service;


import com.ecommerce.api.exception.domain.ProductException;
import com.ecommerce.config.QuantumLockManager;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class ProductService {

    private final ProductRepository productRepository;

    private final QuantumLockManager quantumLockManager;

    public ProductService(ProductRepository productRepository, QuantumLockManager quantumLockManager) {
        this.productRepository = productRepository;
        this.quantumLockManager = quantumLockManager;
    }

    @Cacheable(value = "products", key = "#productId", unless = "#result == null")
    public Product getProduct(Long productId) {
        return productRepository.getProduct(productId).orElseThrow(
                () -> new ProductException.ServiceException("상품을 찾을 수 없습니다.")
        );
    }

    @Transactional
    @Cacheable(value = "products", key = "'popularProducts'", unless = "#result.isEmpty()")
    public List<Product> getPopularProducts() {
        return productRepository.getPopularProducts();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allProducts", unless = "#result.isEmpty()")
    public List<Product> getProducts() {
        return productRepository.getProducts();
    }


    public Product deductStock(Product product, Integer quantity) {
        String lockKey = "product:" + product.getId();
        Duration timeout = Duration.ofSeconds(5);
        try {
            return quantumLockManager.executeWithLock(lockKey, timeout, () -> {
                Product myProduct = getProduct(product.getId());
                myProduct.deductStock(quantity);
                return productRepository.save(myProduct).orElseThrow(
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
    @CachePut(value = "products", key = "#product.id")
    @CacheEvict(value = {"popularProducts", "allProducts"}, allEntries = true)
    public Product saveAndGet(Product product) {
        return productRepository.save(product).orElseThrow(
                () -> new ProductException.ServiceException("상품 저장에 실패했습니다.")
        );
    }

    @Transactional
    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

}
