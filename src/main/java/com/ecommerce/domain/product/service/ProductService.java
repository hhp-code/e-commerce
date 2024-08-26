package com.ecommerce.domain.product.service;


import com.ecommerce.domain.product.ProductDomainMapper;
import com.ecommerce.domain.product.Product;
import com.ecommerce.infra.product.entity.ProductEntity;
import com.ecommerce.interfaces.exception.domain.ProductException;
import com.ecommerce.domain.product.service.repository.ProductRepository;
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

    @Transactional
    @Cacheable(value = "products", key = "#productId", unless = "#result == null")
    public Product getProduct(Long productId) {
        ProductEntity product = productRepository.getProduct(productId).orElseThrow(
                () -> new ProductException.ServiceException("상품을 찾을 수 없습니다.")
        );
        return ProductDomainMapper.toWriteModel(product);
    }

    @Transactional
    @Cacheable(value = "products", key = "'popularProducts'", unless = "#result.isEmpty()")
    public List<Product> getPopularProducts() {
        List<ProductEntity> popularProducts = productRepository.getPopularProducts();
        return  ProductDomainMapper.toWriteModels(popularProducts);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allProducts", unless = "#result.isEmpty()")
    public List<Product> getProducts() {
        List<ProductEntity> products = productRepository.getProducts();
        return ProductDomainMapper.toWriteModels(products);
    }


    @Transactional
    @Caching(
            put = {@CachePut(value = "products", key = "#product.id")},
            evict = {@CacheEvict(value = {"popularProducts", "allProducts"}, allEntries = true)}
    )
    public Product saveAndGet(Product product) {
        ProductEntity entity = ProductDomainMapper.toEntity(product);
        ProductEntity productEntity = productRepository.save(entity).orElseThrow(
                () -> new ProductException.ServiceException("상품 저장에 실패했습니다.")
        );
        return ProductDomainMapper.toWriteModel(productEntity);
    }


}
