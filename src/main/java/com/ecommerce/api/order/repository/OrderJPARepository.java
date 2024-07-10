package com.ecommerce.api.order.repository;

import com.ecommerce.api.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderJPARepository extends JpaRepository<Order, Long> {
}
