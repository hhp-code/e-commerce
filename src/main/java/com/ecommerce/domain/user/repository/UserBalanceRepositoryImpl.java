package com.ecommerce.domain.user.repository;

import com.ecommerce.domain.user.QUser;
import com.ecommerce.domain.user.service.repository.UserBalanceRepository;
import com.ecommerce.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class UserBalanceRepositoryImpl implements UserBalanceRepository {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    public UserBalanceRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<BigDecimal> getAmountByUserId(Long userId) {
        return Optional.ofNullable(queryFactory
                .select(user.balance)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne());
    }

    @Override
    @Transactional
    public Optional<User> saveChargeAmount(Long userId, BigDecimal amount) {
        long updatedCount = queryFactory
                .update(user)
                .set(user.balance, user.balance.add(amount))
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
}