package com.ecommerce.domain.user;

import com.ecommerce.infra.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserDomainMapper {
    public static UserWrite toWriteModel(UserEntity user) {
        return new UserWrite(user.getUsername(), user.getInitialBalance());

    }

    public static UserEntity toEntity(UserWrite user) {
        return new UserEntity(user.getUsername(), user.getInitialBalance());
    }

    public static List<UserWrite> toWriteModels(List<UserEntity> all) {
        List<UserWrite> userWrites = new ArrayList<>();
        for (UserEntity userEntity : all) {
            userWrites.add(toWriteModel(userEntity));
        }
        return userWrites;
    }
}
