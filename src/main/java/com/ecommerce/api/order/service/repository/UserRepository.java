package com.ecommerce.api.order.service.repository;

import com.ecommerce.api.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(Long id);

    Optional<User> save(User testUser);

}
