package com.ecommerce.api.coupon.repository;

import com.ecommerce.api.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface CouponJPARepository extends JpaRepository<Coupon, Long> {
}
