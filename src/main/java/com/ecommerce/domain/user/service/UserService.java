package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.ecommerce.interfaces.exception.domain.UserException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    @Cacheable(value = "users", key = "#userId", unless = "#result == null")
    public User getUser(Long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }




    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public User saveUser(User user) {
        return userRepository.save(user).orElseThrow(
                () -> new RuntimeException("사용자 정보를 찾을 수 없습니다.")
        );
    }
    @CacheEvict(value = {"users", "userCoupons"}, allEntries = true)
    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }



    public List<User> getAllUsers() {
        return userRepository.getAll();
    }

    public User save(User user) {
        return userRepository.save(user).orElseThrow(
                () -> new UserException("사용자 정보를 찾을 수 없습니다.")
        );
    }

    @Transactional
    public User getPoint(Long id) {
        return userRepository.getUser(id).orElseThrow(
                () -> new UserException("사용자 정보를 찾을 수 없습니다.")
        );
    }
}
