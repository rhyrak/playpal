package com.example.playpal.unit.game.controller.controller;

import com.example.playpal.common.exception.ResourceNotFoundException;
import com.example.playpal.game.model.Game;
import com.example.playpal.game.model.dto.CreateGameRequest;
import com.example.playpal.game.model.dto.UpdateGameRequest;
import com.example.playpal.game.service.impl.GameServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GameControllerTest {

    @MockBean
    private GameServiceImpl gameService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private final List<Game> mockGames = List.of(
            Game.builder().id(1L).title("Game 1").description("Game 1 description").build(),
            Game.builder().id(2L).title("Game 2").description("Game 2 description").build()
    );


    @ParameterizedTest
    @CsvSource({
            "POST,/api/v1/games",
            "GET,/api/v1/games",
            "GET,/api/v1/games/1",
            "PUT,/api/v1/games/1",
            "DELETE,/api/v1/games/1",})
    public void whenRequestAsGuest_thenUnauthorized(String method, String uri) throws Exception {
        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .request(HttpMethod.valueOf(method), uri)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // Verify
        Mockito.verifyNoInteractions(gameService);
    }

    @Test
    @WithMockUser
    public void givenValidCreateGameRequest_whenCreateGameAsUser_thenForbidden() throws Exception {
        // Given
        var request = new CreateGameRequest("Game 1", "Game 1 description");

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/games")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Verify
        Mockito.verify(gameService, Mockito.never()).createGame(Mockito.any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenValidCreateGameRequest_whenCreateGameAsAdmin_thenCreated() throws Exception {
        // Given
        var request = new CreateGameRequest("Game", "Description");

        // Mock
        Mockito.when(gameService.createGame(request)).thenReturn(new Game(1L, "Game", "Description"));

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/games")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));

        // Verify
        Mockito.verify(gameService, Mockito.times(1)).createGame(request);
    }

    @Test
    @WithMockUser
    public void givenInvalidCreateGameRequest_whenCreateGame_thenBadRequest() throws Exception {
        // Given
        var request = new CreateGameRequest("", "Game 1 description");

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/games")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Verify
        Mockito.verify(gameService, Mockito.never()).createGame(Mockito.any());
    }

    @Test
    @WithMockUser
    public void givenInvalidUpdateGameRequest_whenUpdateGame_thenBadRequest() throws Exception {
        // Given
        var request = UpdateGameRequest.builder().build();

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/games/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Verify
        Mockito.verify(gameService, Mockito.never()).updateGame(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @WithMockUser
    public void givenValidUpdateGameRequest_whenUpdateGameAsUser_thenForbidden() throws Exception {
        // Given
        var request = UpdateGameRequest.builder().title("not blank").build();

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/games/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Verify
        Mockito.verify(gameService, Mockito.never()).updateGame(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenValidUpdateGameRequest_whenUpdateGameAsAdmin_thenOk() throws Exception {
        // Given
        var request = UpdateGameRequest.builder().title("not blank").build();

        // Mock
        var updatedGame = new Game(1L, "not blank", "");
        Mockito.when(gameService.updateGame(1L, request)).thenReturn(updatedGame);

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/games/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify
        Mockito.verify(gameService, Mockito.times(1)).updateGame(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @WithMockUser
    public void whenGetAllGamesAsUser_thenSuccess() throws Exception {
        // Mock
        Mockito.when(gameService.getAllGames()).thenReturn(mockGames);

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/games")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(mockGames.getFirst().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(mockGames.getFirst().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(mockGames.get(1).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value(mockGames.get(1).getTitle()));


        // Verify
        Mockito.verify(gameService, Mockito.times(1)).getAllGames();
    }

    @Test
    @WithMockUser
    public void givenExistingGameId_whenGetGameByIdAsUser_thenSuccess() throws Exception {
        // Given
        var gameId = 1L;

        // Mock
        Mockito.when(gameService.getGameById(gameId)).thenReturn(mockGames.getFirst());

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/games/{id}", gameId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockGames.getFirst().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockGames.getFirst().getTitle()));


        // Verify
        Mockito.verify(gameService, Mockito.times(1)).getGameById(gameId);
    }

    @Test
    @WithMockUser
    public void givenNonExistentGameId_whenGetGameByIdAsUser_thenNotFound() throws Exception {
        // Given
        var gameId = -1L;

        // Mock
        Mockito.when(gameService.getGameById(gameId)).thenThrow(ResourceNotFoundException.class);

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/games/{id}", gameId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());


        // Verify
        Mockito.verify(gameService, Mockito.times(1)).getGameById(gameId);
    }

    @Test
    @WithMockUser
    public void givenGameId_whenDeleteGameAsUser_thenForbidden() throws Exception {
        // Given
        var gameId = 1L;

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/v1/games/{id}", gameId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Verify
        Mockito.verify(gameService, Mockito.never()).deleteGame(Mockito.anyLong());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenExistingGameId_whenDeleteGameAsAdmin_thenSuccess() throws Exception {
        // Given
        var gameId = 1L;

        // Mock
        Mockito.doNothing().when(gameService).deleteGame(gameId);

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/v1/games/{id}", gameId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        // Verify
        Mockito.verify(gameService, Mockito.times(1)).deleteGame(gameId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void givenNonExistingGameId_whenDeleteGameAsAdmin_thenNotFound() throws Exception {
        // Given
        var gameId = -1L;

        // Mock
        Mockito.doThrow(ResourceNotFoundException.class).when(gameService).deleteGame(gameId);

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/v1/games/{id}", gameId)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Verify
        Mockito.verify(gameService, Mockito.times(1)).deleteGame(gameId);
    }
}