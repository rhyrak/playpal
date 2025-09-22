package com.example.playpal.game.controller;

import com.example.playpal.common.docs.ForbiddenApiResponse;
import com.example.playpal.common.docs.NotFoundApiResponse;
import com.example.playpal.common.docs.UnauthorizedApiResponse;
import com.example.playpal.game.model.dto.CreateGameRequest;
import com.example.playpal.game.model.dto.GameResponse;
import com.example.playpal.game.model.dto.UpdateGameRequest;
import com.example.playpal.game.model.mapper.GameToGameResponseMapper;
import com.example.playpal.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Bearer",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER)
@SecurityRequirement(name = "Bearer")
public class GameController {
    private final GameService gameService;

    @Operation(
            summary = "Create a new game",
            description = "Creates a new game and saves it to the database. Accessible by ADMIN only."
    )
    @ForbiddenApiResponse
    @UnauthorizedApiResponse
    @ApiResponse(responseCode = "201", description = "Game successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid game details provided")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createGame(@Valid @RequestBody CreateGameRequest game) {
        var createdGame = gameService.createGame(game);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdGame.getId()).toUri()).build();
    }

    @Operation(
            summary = "Update a game by ID",
            description = "Updates an existing game with given ID. Accessible by ADMIN only."
    )
    @ForbiddenApiResponse
    @UnauthorizedApiResponse
    @ApiResponse(responseCode = "200", description = "Game successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid game details provided")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateGame(@PathVariable Long id, @Valid @RequestBody UpdateGameRequest request) {
        gameService.updateGame(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get game by ID",
            description = "Retrieves a game. Accessible by both ADMIN and USER roles.")
    @ApiResponse(responseCode = "200", description = "Game successfully retrieved")
    @NotFoundApiResponse(description = "Game is not found")
    @UnauthorizedApiResponse
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        var game = gameService.getGameById(id);
        return ResponseEntity.ok(GameToGameResponseMapper.INSTANCE.map(game));
    }

    @Operation(
            summary = "Get all games",
            description = "Retrieves a list of games. Accessible by both ADMIN and USER roles."
    )
    @UnauthorizedApiResponse
    @ApiResponse(responseCode = "200", description = "Games successfully retrieved")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GameResponse>> getAllGames() {
        var games = gameService.getAllGames();
        return ResponseEntity.ok(GameToGameResponseMapper.INSTANCE.map(games));
    }

    @Operation(
            summary = "Delete a game",
            description = "Deletes a game by its ID. Accessible by ADMIN only."
    )
    @ForbiddenApiResponse
    @UnauthorizedApiResponse
    @ApiResponse(responseCode = "204", description = "Game successfully deleted")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
