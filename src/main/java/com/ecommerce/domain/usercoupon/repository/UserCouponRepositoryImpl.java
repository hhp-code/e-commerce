package com.ecommerce.domain.usercoupon.repository;

import com.ecommerce.domain.coupon.service.repository.UserCouponRepository;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.usercoupon.QUserCoupon;
import com.ecommerce.domain.usercoupon.UserCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJPARepository userCouponJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QUserCoupon qUserCoupon = QUserCoupon.userCoupon;
    public UserCouponRepositoryImpl(UserCouponJPARepository userCouponJPARepository, JPAQueryFactory queryFactory) {
        this.userCouponJPARepository = userCouponJPARepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<UserCoupon> getCouponByUser(User user, Coupon coupon) {
        UserCoupon result = queryFactory
                .selectFrom(qUserCoupon)
                .where(qUserCoupon.user.eq(user)
                        .and(qUserCoupon.coupon.eq(coupon)))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<UserCoupon> getAllCouponsByUserId(Long userId) {
        return queryFactory
                .selectFrom(qUserCoupon)
                .where(qUserCoupon.user.id.eq(userId))
                .fetch();
    }


    @Override
    public Optional<UserCoupon> save(UserCoupon userCoupon) {
       return Optional.of(userCouponJPARepository.save(userCoupon));
    }
}
