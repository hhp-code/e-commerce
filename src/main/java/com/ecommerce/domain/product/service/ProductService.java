package com.ecommerce.domain.product.service;


import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.product.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
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


    @Transactional
    public List<Product> getPopularProducts() {
        return productRepository.getPopularProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        int updatedAvailableRows = productRepository.decreaseAvailableStock(productId, quantity);
        if (updatedAvailableRows == 0) {
            throw new RuntimeException("상품의 재고가 부족합니다. 상품 ID: " + productId);
        }
        int updatedReservedRows = productRepository.increaseReservedStock(productId, quantity);
        if (updatedReservedRows == 0) {
            throw new RuntimeException("상품의 예약 재고가 전환되지 않았습니다. 상품 ID: " + productId);
        }

    }


    @Transactional
    public void increaseStock(Long id, Integer quantity) {
        int updatedReservedRows = productRepository.decreaseReservedStock(id, quantity);
        if (updatedReservedRows == 0) {
            throw new RuntimeException("상품의 예약 재고가 부족합니다. 상품 ID: " + id);
        }
        int updatedAvailableRows = productRepository.increaseAvailableStock(id, quantity);
        if (updatedAvailableRows == 0) {
            throw new RuntimeException("상품의 재고가 전환되지 않았습니다. 상품 ID: " + id);
        }

    }
}
