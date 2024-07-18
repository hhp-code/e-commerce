package com.ecommerce.domain.product.repository;

import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.product.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
@Component
public interface ProductJPARepository extends JpaRepository<Product, Long> {

}
