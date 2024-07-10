package com.ecommerce.api.order.repository;

import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;

    public UserRepositoryImpl(UserJPARepository userJPARepository) {
        this.userJPARepository = userJPARepository;
    }

    @Override
    public Optional<User> getById(Long id) {
        return userJPARepository.findById(id);
    }

    @Override
    public Optional<User> save(User testUser) {
        return Optional.of(userJPARepository.save(testUser));
    }
}