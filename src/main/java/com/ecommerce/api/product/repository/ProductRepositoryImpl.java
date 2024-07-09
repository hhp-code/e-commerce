package com.ecommerce.api.product.repository;

import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJPARepository productJPARepository;

    public ProductRepositoryImpl(ProductJPARepository productJPARepository) {
        this.productJPARepository = productJPARepository;
    }

    @Override
    public List<Product> getPopularProducts() {
        return productJPARepository.getByPopular();
    }

    @Override
    public List<Product> getProducts() {
        return productJPARepository.getAll();
    }

    @Override
    public Product getProduct(Long productId) {
        return productJPARepository.findById(productId).orElseThrow(()->new RuntimeException("ProductRequest not found"));
    }

    @Override
    public Product save(Product oldProduct) {
        productJPARepository.save(oldProduct);
        return productJPARepository.findById(oldProduct.getId()).orElseThrow(()->new RuntimeException("ProductRequest not found"));
    }

    @Override
    public void deleteAll() {
        productJPARepository.deleteAll();
    }
}
