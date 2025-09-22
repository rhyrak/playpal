package com.example.playpal.game.repository;

import com.example.playpal.game.model.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
    Optional<GameEntity> findByTitle(String title);
}
