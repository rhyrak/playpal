package com.example.playpal.game.service.impl;

import com.example.playpal.common.exception.ResourceNotFoundException;
import com.example.playpal.game.model.Game;
import com.example.playpal.game.model.dto.CreateGameRequest;
import com.example.playpal.game.model.dto.UpdateGameRequest;
import com.example.playpal.game.model.entity.GameEntity;
import com.example.playpal.game.model.mapper.GameEntityToGameMapper;
import com.example.playpal.game.repository.GameRepository;
import com.example.playpal.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;

    @Override
    public Game createGame(CreateGameRequest game) {
        var gameEntity = GameEntity.builder()
                .title(game.getTitle())
                .description(game.getDescription())
                .build();

        var savedGame = gameRepository.save(gameEntity);
        return GameEntityToGameMapper.INSTANCE.map(savedGame);
    }

    @Override
    public Game updateGame(Long id, UpdateGameRequest updateGameRequest) {
        var gameEntity = gameRepository.findById(id).orElseThrow(gameNotFound(id));
        gameEntity.setTitle(updateGameRequest.getTitle());
        gameEntity.setDescription(updateGameRequest.getDescription());

        gameRepository.save(gameEntity);
        return GameEntityToGameMapper.INSTANCE.map(gameEntity);
    }

    @Override
    public Game getGameById(Long id) {
        var gameEntity = gameRepository.findById(id).orElseThrow(gameNotFound(id));

        return GameEntityToGameMapper.INSTANCE.map(gameEntity);
    }

    @Override
    public List<Game> getAllGames() {
        var gameEntityList = gameRepository.findAll();
        return GameEntityToGameMapper.INSTANCE.map(gameEntityList);
    }

    @Override
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id))
            throw gameNotFound(id).get();

        gameRepository.deleteById(id);
    }

    private Supplier<ResourceNotFoundException> gameNotFound(Long id) {
        return () -> new ResourceNotFoundException("Game with id " + id + " not found");
    }
}
