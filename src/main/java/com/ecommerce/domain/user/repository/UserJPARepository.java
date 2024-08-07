package com.ecommerce.domain.user.repository;

import com.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJPARepository extends JpaRepository<User, Long> {
}
