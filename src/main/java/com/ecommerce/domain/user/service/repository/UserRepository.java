package com.ecommerce.domain.user.service.repository;

import com.ecommerce.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(Long id);

    Optional<User> save(User testUser);

}
