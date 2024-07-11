package com.ecommerce.api.product.repository;

import com.ecommerce.api.domain.Product;
import jakarta.persistence.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public interface ProductJPARepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> getAll();

    @Query(value = "SELECT p.* FROM Product p " +
            "JOIN (SELECT o.product_id, SUM(o.quantity) as total_quantity " +
            "      FROM Order o " +
            "      WHERE o.order_date BETWEEN :startDate AND :endDate " +
            "      GROUP BY o.product_id " +
            "      ORDER BY total_quantity DESC " +
            "      LIMIT :limit) top_products " +
            "ON p.id = top_products.product_id " +
            "ORDER BY top_products.total_quantity DESC",
            nativeQuery = true)
    List<Product> getTopSellingProductsLast3Days(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("limit") int limit);

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.availableStock = p.availableStock - :quantity WHERE p.id = :id AND p.availableStock >= :quantity")
    int decreaseStock(@Param("id")Long id,@Param("quantity") int orderedQuantity);
}
