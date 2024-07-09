package com.ecommerce.api.product.service.repository;

import com.ecommerce.domain.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> getPopularProducts();

    List<Product> getProducts();

    Product getProduct(Long productId);

    Product save(Product oldProduct);

    void deleteAll();
}
