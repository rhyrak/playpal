package com.example.playpal.auth.repository;

import com.example.playpal.auth.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsUserEntityByEmail(String email);

    boolean existsUserEntityByUsername(String username);

    Optional<UserEntity> findUserEntityByEmail(String email);
}
