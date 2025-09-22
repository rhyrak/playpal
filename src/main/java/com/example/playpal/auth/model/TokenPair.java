package com.example.playpal.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
