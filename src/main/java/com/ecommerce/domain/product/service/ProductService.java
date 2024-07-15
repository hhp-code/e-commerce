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
    public void decreaseStock(Product product, int quantity) {
        productUpdate(productRepository.decreaseAvailableStock(product.getId(), quantity),
                "상품의 재고가 부족합니다. 상품 ID: ", product.getId());
        productUpdate(productRepository.increaseReservedStock(product.getId(), quantity),
                "상품의 예약 재고가 전환되지 않았습니다. 상품 ID: ", product.getId());

    }


    @Transactional
    public void increaseStock(Product product, Integer quantity) {
        long id = product.getId();
        productUpdate(productRepository.decreaseReservedStock(id, quantity), "상품의 예약 재고가 부족합니다. 상품 ID: ", id);
        productUpdate(productRepository.increaseAvailableStock(id, quantity), "상품의 재고가 전환되지 않았습니다. 상품 ID: ", id);

    }

    private void productUpdate(int productRepository, String x, long id) {
        if (productRepository == 0) {
            throw new RuntimeException(x + id);
        }
    }

    public Product saveAndGet(Product testProduct) {
        return productRepository.save(testProduct).orElseThrow(
                () -> new RuntimeException("Product not found")
        );
    }
}
