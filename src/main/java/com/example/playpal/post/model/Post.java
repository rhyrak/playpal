package com.example.playpal.post.model;

import com.example.playpal.auth.model.User;
import com.example.playpal.game.model.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private User user;
    private Game game;
    private String description;
}
