package com.ecommerce.infra.coupon;

import com.ecommerce.infra.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponJPARepository extends JpaRepository<CouponEntity, Long> {
    @Query("SELECT c.quantity FROM CouponEntity c WHERE c.id = :couponId")
    Integer getRemainingQuantity(Long couponId);
}
