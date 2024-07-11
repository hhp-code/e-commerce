package com.ecommerce.api.product.service;


import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.api.domain.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(Long productId) {
        return productRepository.getProduct(productId).orElseThrow(
                () -> new RuntimeException("Product not found")
        );
    }


    public List<Product> getPopularProducts() {
        return productRepository.getPopularProducts();
    }

    public List<Product> getProducts() {
        return productRepository.getProducts();
    }
}
