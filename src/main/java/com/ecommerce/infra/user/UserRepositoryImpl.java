package com.ecommerce.infra.user;

import com.ecommerce.domain.user.QUser;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
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
        User user1 = queryFactory.select(user).from(user).where(user.id.eq(id)).fetchOne();
        return Optional.ofNullable(user1);
    }

    @Override
    public Optional<User> save(User user) {
        return Optional.of(userJPARepository.save(user));
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
    public Optional<User> getUser(Long userId) {
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