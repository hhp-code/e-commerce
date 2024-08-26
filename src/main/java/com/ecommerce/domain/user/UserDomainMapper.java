package com.ecommerce.domain.user;

import com.ecommerce.infra.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserDomainMapper {
    public static User toWriteModel(UserEntity user) {
        return new User(user.getUsername(), user.getInitialBalance());

    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(user.getUsername(), user.getInitialBalance());
    }

    public static List<User> toWriteModels(List<UserEntity> all) {
        List<User> users = new ArrayList<>();
        for (UserEntity userEntity : all) {
            users.add(toWriteModel(userEntity));
        }
        return users;
    }
}
