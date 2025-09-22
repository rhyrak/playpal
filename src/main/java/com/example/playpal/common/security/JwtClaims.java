package com.example.playpal.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtClaims {
    ISSUED_AT("iat"),
    EXPIRES_AT("exp"),
    ALGORITHM("alg"),
    TYP("typ"),
    JWT_ID("jti"),
    USER_ID("user_id"),
    USERNAME("username"),
    USER_TYPE("user_type");

    private final String value;
}
