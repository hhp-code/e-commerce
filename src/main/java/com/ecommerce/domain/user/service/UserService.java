package com.ecommerce.domain.user.service;

import com.ecommerce.domain.coupon.CouponWrite;
import com.ecommerce.domain.user.UserDomainMapper;
import com.ecommerce.domain.user.UserWrite;
import com.ecommerce.domain.user.service.repository.UserRepository;
import com.ecommerce.infra.user.entity.UserEntity;
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

    @Transactional(readOnly = true)
    public List<CouponWrite> getUserCoupons(Long userId) {
        UserEntity user = userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
        UserWrite writeModel = UserDomainMapper.toWriteModel(user);
        return writeModel.getCoupons();
    }
    @Transactional
    @Cacheable(value = "users", key = "#userId", unless = "#result == null")
    public UserWrite getUser(Long userId) {
        UserEntity user = userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
        return UserDomainMapper.toWriteModel(user);
    }


    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public UserWrite saveUser(UserWrite user) {
        UserEntity entity = UserDomainMapper.toEntity(user);
        UserEntity userEntity = userRepository.save(entity).orElseThrow(
                () -> new RuntimeException("사용자 정보를 찾을 수 없습니다.")
        );
        return UserDomainMapper.toWriteModel(userEntity);
    }

    @CacheEvict(value = {"users", "userCoupons"}, allEntries = true)
    public void saveAll(List<UserEntity> users) {
        userRepository.saveAll(users);
    }



    public List<UserWrite> getAllUsers() {
        List<UserEntity> all = userRepository.getAll();
        return UserDomainMapper.toWriteModels(all);
    }


    @Transactional
    public UserWrite getPoint(Long id) {
        UserEntity userEntity = userRepository.getUser(id).orElseThrow(
                () -> new UserException("사용자 정보를 찾을 수 없습니다.")
        );
        return UserDomainMapper.toWriteModel(userEntity);
    }
}
