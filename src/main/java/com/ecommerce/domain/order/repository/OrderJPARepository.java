package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJPARepository extends JpaRepository<Order, Long> {

}
