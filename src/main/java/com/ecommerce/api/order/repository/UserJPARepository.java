package com.ecommerce.api.order.repository;

import com.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserJPARepository extends JpaRepository<User, Long> {
}
