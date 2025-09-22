package com.example.playpal.auth.service.impl;

import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.LoginRequest;
import com.example.playpal.auth.model.mapper.UserEntityToClaimsMapper;
import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.auth.service.LoginService;
import com.example.playpal.auth.service.TokenService;
import com.example.playpal.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public TokenPair login(LoginRequest loginRequest) {
        var userEntity = userRepository.findUserEntityByEmail(loginRequest.getEmail());
        if (userEntity.isEmpty())
            throw new UnauthorizedException("Invalid email or password.");

        if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.get().getPassword()))
            throw new UnauthorizedException("Invalid email or password.");

        var claims = UserEntityToClaimsMapper.INSTANCE.map(userEntity.get());

        return tokenService.generateTokenPair(claims);
    }
}
