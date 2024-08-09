package com.ecommerce.infra.order;

import com.ecommerce.infra.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJPARepository extends JpaRepository<OrderEntity, Long> {
}
