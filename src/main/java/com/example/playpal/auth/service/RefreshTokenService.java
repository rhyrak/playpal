package com.example.playpal.auth.service;

import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.TokenRefreshRequest;

public interface RefreshTokenService {
    TokenPair refreshToken(TokenRefreshRequest tokenRefreshRequest);
}
