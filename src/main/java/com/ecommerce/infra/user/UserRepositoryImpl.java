package com.ecommerce.infra.user;

import com.ecommerce.domain.user.service.repository.UserRepository;
import com.ecommerce.infra.coupon.entity.CouponEntity;
import com.ecommerce.infra.coupon.entity.QCouponEntity;
import com.ecommerce.infra.user.entity.QUserEntity;
import com.ecommerce.infra.user.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QUserEntity user = QUserEntity.userEntity;
    private final QCouponEntity coupon = QCouponEntity.couponEntity;

    public UserRepositoryImpl(UserJPARepository userJPARepository, JPAQueryFactory queryFactory) {
        this.userJPARepository = userJPARepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<UserEntity> getById(Long id) {
        UserEntity user1 = queryFactory.select(user).from(user).where(user.id.eq(id)).fetchOne();
        return Optional.ofNullable(user1);
    }

    @Override
    public Optional<UserEntity> save(UserEntity user) {
        return Optional.of(userJPARepository.save(user));
    }

    @Override
    public Optional<CouponEntity> getCouponByUser(long userId, long couponId) {
        return Optional.ofNullable(queryFactory
                .select(coupon)
                .from(user)
                .join(user.coupons, coupon)
                .where(user.id.eq(userId).and(coupon.id.eq(couponId)))
                .fetchOne());
    }



    @Override
    public void saveAll(List<UserEntity> users) {
        userJPARepository.saveAll(users);
    }

    @Override
    public List<UserEntity> getAll() {
        return userJPARepository.findAll();
    }

    @Override
    public Optional<UserEntity> getUser(Long userId) {
        return userJPARepository.findById(userId);
    }


    @Override
    public Optional<BigDecimal> getAmountByUserId(Long userId) {
        return Optional.ofNullable(queryFactory
                .select(user.point)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne());
    }

}