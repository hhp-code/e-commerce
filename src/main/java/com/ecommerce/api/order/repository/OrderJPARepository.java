package com.ecommerce.api.order.repository;

import com.ecommerce.api.domain.Order;
import com.ecommerce.api.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface OrderJPARepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id = :id AND o.status = :orderStatus")
    Optional<Order> findByUserIdAndStatus(Long id, OrderStatus orderStatus);
}
