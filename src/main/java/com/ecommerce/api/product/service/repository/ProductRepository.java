package com.ecommerce.api.product.service.repository;

import com.ecommerce.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getPopularProducts();

    List<Product> getProducts();

    Optional<Product> getProduct(Long productId);

    Optional<Product> save(Product oldProduct);

    void deleteAll();

}
