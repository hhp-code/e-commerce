package com.ecommerce.domain.user.service.repository;

import com.ecommerce.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(Long id);

    Optional<User> save(User testUser);



    void saveAll(List<User> users);

    List<User> getAll();

    Optional<User> getUser(Long userId);
}
