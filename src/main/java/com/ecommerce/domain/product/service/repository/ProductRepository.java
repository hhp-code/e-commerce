package com.ecommerce.domain.product.service.repository;

import com.ecommerce.infra.product.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<ProductEntity> getPopularProducts();

    List<ProductEntity> getProducts();

    Optional<ProductEntity> getProduct(Long productId);

    Optional<ProductEntity> save(ProductEntity oldProduct);

    void deleteAll();

    void saveAll(List<ProductEntity> products);

    List<ProductEntity> getAll();

    Optional<ProductEntity> saveProduct(ProductEntity testProduct);
}
