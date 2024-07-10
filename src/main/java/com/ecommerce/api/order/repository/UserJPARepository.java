package com.ecommerce.api.order.repository;

import com.ecommerce.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserJPARepository extends JpaRepository<User, Long> {
}
