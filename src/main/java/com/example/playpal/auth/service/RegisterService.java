package com.example.playpal.auth.service;

import com.example.playpal.auth.model.TokenPair;
import com.example.playpal.auth.model.dto.RegisterRequest;

public interface RegisterService {
    TokenPair registerUser(RegisterRequest registerRequest);
}
