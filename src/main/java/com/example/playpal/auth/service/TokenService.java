package com.example.playpal.auth.service;

import com.example.playpal.auth.model.TokenPair;
import io.jsonwebtoken.Claims;

public interface TokenService {
    TokenPair generateTokenPair(final Claims claims);

    Claims validateAndExtractClaims(final String token);
}
