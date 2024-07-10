package com.ecommerce.api.order.service.repository;

import com.ecommerce.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(Long id);

    Optional<User> save(User testUser);

}
