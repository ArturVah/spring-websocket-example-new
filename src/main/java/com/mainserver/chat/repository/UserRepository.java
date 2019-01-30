package com.mainserver.chat.repository;

import com.mainserver.chat.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
}
