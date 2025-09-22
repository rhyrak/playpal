package com.example.playpal.game.service;


import com.example.playpal.game.model.Game;
import com.example.playpal.game.model.dto.CreateGameRequest;
import com.example.playpal.game.model.dto.UpdateGameRequest;

import java.util.List;

public interface GameService {

    Game createGame(CreateGameRequest game);

    Game updateGame(Long id, UpdateGameRequest game);

    Game getGameById(Long id);

    List<Game> getAllGames();

    void deleteGame(Long id);
}
