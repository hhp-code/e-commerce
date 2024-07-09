package com.ecommerce.api.product.repository;

import com.ecommerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductJPARepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> getAll();

    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> getByPopular();
}
