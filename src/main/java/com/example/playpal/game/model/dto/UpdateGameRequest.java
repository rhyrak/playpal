package com.example.playpal.game.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateGameRequest {
    @NotBlank(message = "title field cannot be empty")
    private String title;
    private String description;
}
