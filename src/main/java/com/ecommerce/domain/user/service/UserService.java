package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Coupon> getUserCoupons(Long userId) {
        User user = getUser(userId);
        return user.getCoupons();
    }
    @Transactional
    public User getUser(Long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }




    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user).orElseThrow(
                () -> new RuntimeException("사용자 정보를 찾을 수 없습니다.")
        );
    }

    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public List<User> getAllUsers() {
        return userRepository.getAll();
    }
}
