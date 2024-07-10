package com.ecommerce.api.balance.repository;

import com.ecommerce.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public interface BalanceJPARepository extends JpaRepository<User, Long> {
    @Query("SELECT balance FROM User WHERE id = ?1")
    BigDecimal findAmountById(Long id);

    @Query("UPDATE User SET balance = ?1 WHERE id = ?2")
    Optional<User> saveChargeAmount(User user);
}
