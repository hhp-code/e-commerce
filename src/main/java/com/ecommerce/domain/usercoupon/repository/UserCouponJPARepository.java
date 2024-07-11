package com.ecommerce.domain.usercoupon.repository;

import com.ecommerce.domain.usercoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJPARepository extends JpaRepository<UserCoupon, Long> {

}
