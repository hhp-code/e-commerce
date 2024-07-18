package com.ecommerce.domain.product.service.repository;

import com.ecommerce.domain.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getPopularProducts();

    List<Product> getProducts();

    Optional<Product> getProduct(Long productId);

    Optional<Product> save(Product oldProduct);

    void deleteAll();


    void saveAll(List<Product> list);
}
