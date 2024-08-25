package com.ecommerce.infra.user;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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



}