package com.ecommerce.api.product.repository;

import com.ecommerce.api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ProductJPARepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> getAll();

    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> getByPopular();

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.availableStock = p.availableStock - :quantity WHERE p.id = :id AND p.availableStock >= :quantity")
    int decreaseStock(@Param("id")Long id,@Param("quantity") int orderedQuantity);
}
