package com.example.playpal.auth.service;

import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.LoginRequest;

public interface LoginService {
    TokenPair login(final LoginRequest loginRequest);
}
