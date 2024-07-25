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

import java.math.BigDecimal;
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
    public Optional<Coupon> getCouponByUser(long userId, long couponId) {
        return Optional.ofNullable(queryFactory
                .select(coupon)
                .from(user)
                .join(user.coupons, coupon)
                .where(user.id.eq(userId).and(coupon.id.eq(couponId)))
                .fetchOne());
    }

    @Override
    @Transactional
    public void deleteAll() {
        userJPARepository.deleteAll();
    }

    @Override
    public void saveAll(List<User> users) {
        userJPARepository.saveAll(users);
    }

    @Override
    public List<User> getAll() {
        return userJPARepository.findAll();
    }


    @Override
    public Optional<BigDecimal> getAmountByUserId(Long userId) {
        return Optional.ofNullable(queryFactory
                .select(user.point)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne());
    }

    @Override
    public Optional<User> saveChargeAmount(Long userId, BigDecimal amount) {
        long updatedCount = queryFactory
                .update(user)
                .set(user.point, user.point.add(amount))
                .where(user.id.eq(userId))
                .execute();

        if (updatedCount == 0) {
            return Optional.empty();
        }

        return getUserByRequest(userId);
    }

    @Override
    public Optional<User> getUserByRequest(Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne());
    }

    @Override
    public Optional<BigDecimal> getAmountByUserIdWithLock(Long userId) {
        return Optional.ofNullable(queryFactory
                .select(user.point)
                .from(user)
                .where(user.id.eq(userId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne());
    }

    @Override
    public Optional<User> getByIdWithLock(Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne());
    }
}