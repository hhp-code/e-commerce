package com.ecommerce.api.balance.repository;

import com.ecommerce.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public interface BalanceJPARepository extends JpaRepository<User, Long> {
    @Query("SELECT balance FROM User WHERE id = ?1")
    BigDecimal findAmountById(Long id);

    @Modifying
    @Query("UPDATE User u SET u.balance = :balance WHERE u.id = :id")
    int saveChargeAmount(@Param("balance") BigDecimal balance, @Param("id") Long id);
}
