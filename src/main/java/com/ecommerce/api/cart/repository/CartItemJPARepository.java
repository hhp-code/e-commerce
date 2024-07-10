package com.ecommerce.api.cart.repository;

import com.ecommerce.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface CartItemJPARepository extends JpaRepository<CartItem, Long> {
}
