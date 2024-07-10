package com.ecommerce.api.coupon.repository;

import com.ecommerce.api.domain.Coupon;
import com.ecommerce.api.domain.User;
import com.ecommerce.api.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserCouponJPARepository extends JpaRepository<UserCoupon, Long> {
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user = ?1 AND uc.coupon = ?2")
    UserCoupon findByUserAndCoupon(User user, Coupon coupon);
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = ?1")
    List<UserCoupon> findAllByUserId(Long userId);
}
