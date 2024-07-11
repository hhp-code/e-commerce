package com.ecommerce.domain.coupon.repository;

import com.ecommerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJPARepository extends JpaRepository<Coupon, Long> {
}
