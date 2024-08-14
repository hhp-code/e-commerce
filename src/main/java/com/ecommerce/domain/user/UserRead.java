package com.ecommerce.domain.user;


import com.ecommerce.domain.coupon.CouponRead;

import java.math.BigDecimal;
import java.util.List;

public class UserRead {
    private Long id;
    private String username;
    private BigDecimal point;
    private List<CouponRead> coupons;
}