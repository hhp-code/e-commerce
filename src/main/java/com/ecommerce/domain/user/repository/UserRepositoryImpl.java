package com.ecommerce.domain.user.repository;

import com.ecommerce.domain.user.QUser;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    public UserRepositoryImpl(UserJPARepository userJPARepository, JPAQueryFactory queryFactory) {
        this.userJPARepository = userJPARepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.id.eq(id))
                .fetchOne());
    }

    @Override
    public Optional<User> save(User user) {
        return Optional.of(userJPARepository.save(user));
    }
}