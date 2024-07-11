package com.ecommerce.api.product.repository;

import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.api.domain.Product;
import jakarta.persistence.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJPARepository productJPARepository;

    public ProductRepositoryImpl(ProductJPARepository productJPARepository) {
        this.productJPARepository = productJPARepository;
    }

    @Override
    public List<Product> getPopularProducts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        return productJPARepository.getTopSellingProductsLast3Days(
                threeDaysAgo, now, 5
        );
    }

    @Override
    public List<Product> getProducts() {
        return productJPARepository.getAll();
    }

    @Override
    public Optional<Product> getProduct(Long productId) {
        return productJPARepository.findById(productId);
    }

    @Override
    public Optional<Product> save(Product oldProduct) {
        productJPARepository.save(oldProduct);
        return productJPARepository.findByName(oldProduct.getName());
    }

    @Override
    public void deleteAll() {
        productJPARepository.deleteAll();
    }

    @Override
    public int decreaseStock(Long id, int orderedQuantity) {
        return productJPARepository.decreaseStock(id, orderedQuantity);
    }

}
