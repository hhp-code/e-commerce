package com.ecommerce.domain.user.repository;

import com.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserJPARepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.coupons WHERE u.id = :userId")
    Optional<User> getUserWithCoupon(Long userId);
}
