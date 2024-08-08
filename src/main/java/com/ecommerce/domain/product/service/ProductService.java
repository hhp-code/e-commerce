package com.ecommerce.domain.product.service;


import com.ecommerce.interfaces.exception.domain.ProductException;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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


    @Transactional
    @Caching(
            put = {@CachePut(value = "products", key = "#product.id")},
            evict = {@CacheEvict(value = {"popularProducts", "allProducts"}, allEntries = true)}
    )
    public Product saveAndGet(Product product) {
        return productRepository.save(product).orElseThrow(
                () -> new ProductException.ServiceException("상품 저장에 실패했습니다.")
        );
    }

    @Transactional
    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    @Transactional
    public Product saveProduct(Product testProduct) {
        return productRepository.saveProduct(testProduct).orElseThrow(
                () -> new ProductException.ServiceException("상품 저장에 실패했습니다.")
        );
    }
}
