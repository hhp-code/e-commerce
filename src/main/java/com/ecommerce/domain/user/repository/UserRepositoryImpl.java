package com.ecommerce.domain.user.repository;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.QCoupon;
import com.ecommerce.domain.user.QUser;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;
    private final QCoupon coupon = QCoupon.coupon;


    public UserRepositoryImpl(UserJPARepository userJPARepository, JPAQueryFactory queryFactory) {
        this.userJPARepository = userJPARepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<User> getById(Long id) {
        User user1 = queryFactory.select(user).from(user).where(user.id.eq(id)).fetchOne();
        return Optional.ofNullable(user1);
    }

    @Override
    public Optional<User> save(User user) {
        return Optional.of(userJPARepository.save(user));
    }

    @Override
    public Optional<Coupon> getCouponByUser(User targetUser, Coupon targetCoupon) {
        return Optional.ofNullable(queryFactory
                .select(coupon)
                .from(user)
                .join(user.coupons, coupon)
                .where(user.eq(targetUser).and(coupon.eq(targetCoupon)))
                .fetchOne());
    }

    @Override
    public List<Coupon> getAllCouponsByUserId(Long userId) {
        return queryFactory
                .select(coupon)
                .from(user)
                .join(user.coupons, coupon)
                .where(user.id.eq(userId))
                .fetch();
    }

    @Override
    public Optional<User> getUserByCoupon(Coupon userCoupon) {
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .join(user.coupons, coupon)
                .where(coupon.eq(userCoupon))
                .fetchOne());
    }

    @Override
    @Transactional
    public void deleteAll() {
        userJPARepository.deleteAll();
    }
}