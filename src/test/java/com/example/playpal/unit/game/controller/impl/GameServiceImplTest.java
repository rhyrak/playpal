package com.example.playpal.unit.game.controller.impl;

import com.example.playpal.common.exception.ResourceNotFoundException;
import com.example.playpal.game.model.Game;
import com.example.playpal.game.model.dto.CreateGameRequest;
import com.example.playpal.game.model.dto.UpdateGameRequest;
import com.example.playpal.game.model.entity.GameEntity;
import com.example.playpal.game.repository.GameRepository;
import com.example.playpal.game.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {
    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameRepository gameRepository;

    private final List<GameEntity> mockGameEntities = List.of(
            new GameEntity(1L, "Game 1", "Game 1 description"),
            new GameEntity(2L, "Game 2", "Game 2 description")
    );
    private final List<Game> mockGames = List.of(
            new Game(1L, "Game 1", "Game 1 description"),
            new Game(2L, "Game 2", "Game 2 description")
    );

    @Test
    public void givenCreateGameRequest_whenCreateGame_thenReturnCreatedGame() {
        // Given
        var createGameRequest = CreateGameRequest.builder()
                .title(mockGameEntities.getFirst().getTitle())
                .description(mockGameEntities.getFirst().getDescription())
                .build();

        // Mock
        Mockito.when(gameRepository.save(Mockito.any(GameEntity.class))).thenReturn(mockGameEntities.getFirst());

        // When
        var savedGame = gameService.createGame(createGameRequest);

        // Then
        assertNotNull(savedGame);
        assertEquals(mockGameEntities.getFirst().getId(), savedGame.getId());
        assertEquals(savedGame.getTitle(), mockGameEntities.getFirst().getTitle());
        assertEquals(savedGame.getDescription(), mockGameEntities.getFirst().getDescription());

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(GameEntity.class));
    }


    @Test
    public void givenUpdateGameRequest_whenUpdateGame_thenReturnUpdatedGame() {
        // Given
        var updateGameRequest = UpdateGameRequest.builder().title("new game").description("new desc").build();

        // Mock
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(mockGameEntities.getFirst()));
        Mockito.when(gameRepository.save(Mockito.any(GameEntity.class)))
                .thenReturn(new GameEntity(1L, "new game", "new desc"));

        // When
        var updatedGame = gameService.updateGame(1L, updateGameRequest);

        // Then
        assertNotNull(updatedGame);
        assertEquals(1L, updatedGame.getId());
        assertEquals("new game", updatedGame.getTitle());
        assertEquals("new desc", updatedGame.getDescription());

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(GameEntity.class));
    }


    @Test
    public void givenValidId_whenDeleteGame_thenDeleteGame() {
        // Mock
        Mockito.when(gameRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(gameRepository).deleteById(Mockito.anyLong());

        // When & Then
        gameService.deleteGame(1L);

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(gameRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void givenInvalidId_whenDeleteGame_thenThrowResourceNotFoundException() {
        // Mock
        Mockito.when(gameRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> gameService.deleteGame(1L));

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(gameRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    public void whenGetAllGames_thenReturnListOfGames() {
        // Mock
        Mockito.when(gameRepository.findAll()).thenReturn(mockGameEntities);

        // When
        var result = gameService.getAllGames();

        // Then
        assertNotNull(result);
        assertEquals(mockGames, result);

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void givenExistingGameId_whenGetGameById_thenReturnGame() {
        // Given
        var gameId = 1L;

        // Mock
        var gameEntity = GameEntity.builder().id(gameId).build();
        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(gameEntity));

        // When
        var result = gameService.getGameById(gameId);

        // Then
        assertNotNull(result);
        assertEquals(gameId, result.getId());

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).findById(gameId);
    }

    @Test
    public void givenNonExistentGameId_whenGetGameById_thenThrowResourceNotFoundException() {
        // Given
        var gameId = -1L;

        // Mock
        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> gameService.getGameById(gameId));

        // Verify
        Mockito.verify(gameRepository, Mockito.times(1)).findById(gameId);
    }
}