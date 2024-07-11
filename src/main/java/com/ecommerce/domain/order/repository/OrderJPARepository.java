package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderJPARepository extends JpaRepository<Order, Long> {

}
