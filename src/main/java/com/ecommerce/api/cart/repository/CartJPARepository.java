package com.ecommerce.api.cart.repository;

import com.ecommerce.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface CartJPARepository extends JpaRepository<Cart, Long>{

}

