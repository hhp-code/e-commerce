package com.ecommerce.domain.user.repository;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.QCoupon;
import com.ecommerce.domain.coupon.service.repository.UserCouponRepository;
import com.ecommerce.domain.user.QUser;
import com.ecommerce.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final JPAQueryFactory queryFactory;
    private final QUser qUser = QUser.user;
    private final QCoupon qCoupon = QCoupon.coupon;
    public UserCouponRepositoryImpl( JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Coupon> getAllCouponsByUserId(Long userId) {
        return Objects.requireNonNull(queryFactory
                .selectFrom(qUser)
                .where(qUser.id.eq(userId))
                .fetchOne()).getCoupons();
    }

    @Override
    public Optional<Coupon> getCouponByUser(User user, Coupon coupon) {
       return Optional.ofNullable(queryFactory
               .select(qCoupon)
               .from(qUser)
               .join(qUser.coupons, qCoupon)
               .where(qUser.eq(user).and(qCoupon.eq(coupon)))
               .fetchOne());
    }


}
