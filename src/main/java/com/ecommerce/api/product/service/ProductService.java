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
        //TODO: 3일동안의 인기를 구가하는 상품을 반환하는 로직을 구현해야 합니다.
        return productRepository.getPopularProducts();
    }

    public List<Product> getProducts() {
        return productRepository.getProducts();
    }
}
