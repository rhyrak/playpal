package com.example.playpal.auth.service.impl;

import com.example.playpal.auth.config.TokenConfiguration;
import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.service.TokenService;
import com.example.playpal.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenConfiguration tokenConfiguration;

    @Override
    public TokenPair generateTokenPair(Claims claims) {
        return TokenPair.builder()
                .accessToken(generateToken(claims, tokenConfiguration.getAccessTokenValiditySeconds()))
                .refreshToken(generateToken(claims, tokenConfiguration.getRefreshTokenValiditySeconds()))
                .build();
    }

    @Override
    public Claims validateAndExtractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(tokenConfiguration.getPublicKey())
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    private String generateToken(final Claims claims, final Long validitySeconds) {
        var currentTimeMillis = System.currentTimeMillis();
        var tokenIssuedAt = new Date(currentTimeMillis);
        var refreshTokenExpiresAt = new Date(currentTimeMillis + validitySeconds * 1000);

        return Jwts.builder()
                .header()
                .type("Bearer")
                .and()
                .issuer(tokenConfiguration.getIssuer())
                .issuedAt(tokenIssuedAt)
                .expiration(refreshTokenExpiresAt)
                .claims(claims)
                .signWith(tokenConfiguration.getPrivateKey(), Jwts.SIG.RS512)
                .compact();
    }
}
