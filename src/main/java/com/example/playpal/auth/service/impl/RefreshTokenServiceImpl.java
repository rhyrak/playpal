package com.example.playpal.auth.service.impl;

import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.TokenRefreshRequest;
import com.example.playpal.auth.model.mapper.UserEntityToClaimsMapper;
import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.auth.service.RefreshTokenService;
import com.example.playpal.auth.service.TokenService;
import com.example.playpal.common.exception.UnauthorizedException;
import com.example.playpal.common.security.JwtClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public TokenPair refreshToken(TokenRefreshRequest tokenRefreshRequest) {
        var claims = tokenService.validateAndExtractClaims(tokenRefreshRequest.getRefreshToken());
        var userEntity = userRepository.findById(claims.get(JwtClaims.USER_ID.getValue(), Long.class)).orElseThrow();

        if (userEntity.getLastLogout().after(claims.getIssuedAt()))
            throw new UnauthorizedException("expired refresh token");
        

        var newClaims = UserEntityToClaimsMapper.INSTANCE.map(userEntity);

        return tokenService.generateTokenPair(newClaims);
    }
}
