package com.example.playpal.auth.service.impl;

import com.example.playpal.auth.exception.UserAlreadyExistsException;
import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.RegisterRequest;
import com.example.playpal.auth.model.entity.UserEntity;
import com.example.playpal.auth.model.mapper.UserEntityToClaimsMapper;
import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.auth.service.RegisterService;
import com.example.playpal.auth.service.TokenService;
import com.example.playpal.common.security.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public TokenPair registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsUserEntityByEmail(registerRequest.getEmail()))
            throw new UserAlreadyExistsException("Email already in use");

        if (userRepository.existsUserEntityByUsername(registerRequest.getUsername()))
            throw new UserAlreadyExistsException("Username already in use");

        var newUser = UserEntity.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .userType(UserType.USER.getValue())
                .lastLogout(new Date(0))
                .build();

        var savedUser = userRepository.save(newUser);

        var claims = UserEntityToClaimsMapper.INSTANCE.map(savedUser);

        return tokenService.generateTokenPair(claims);
    }
}
