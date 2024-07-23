package com.ecommerce.domain.product.service;


import com.ecommerce.api.exception.domain.ProductException;
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
                () -> new ProductException.ServiceException("상품을 찾을 수 없습니다.")
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
    public void deductStock(Product product, Integer quantity) {
        product.deductStock(quantity);
        productRepository.save(product).orElseThrow(
                ()-> new ProductException.ServiceException("재고 차감에 실패했습니다.")
        );
    }


    @Transactional
    public void chargeStock(Product product, Integer quantity) {
        product.chargeStock(quantity);
        productRepository.save(product).orElseThrow(
                () -> new ProductException.ServiceException("재고 입고에 실패했습니다.")
        );
    }

    @Transactional
    public Product saveAndGet(Product testProduct) {
        return productRepository.save(testProduct).orElseThrow(
                () -> new ProductException.ServiceException("상품 저장에 실패했습니다.")
        );
    }
}
