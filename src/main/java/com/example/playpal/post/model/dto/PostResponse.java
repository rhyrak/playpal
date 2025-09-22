package com.example.playpal.post.model.dto;

import com.example.playpal.auth.model.dto.UserResponse;
import com.example.playpal.game.model.dto.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String description;
    private UserResponse user;
    private GameResponse game;
}
