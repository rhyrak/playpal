package com.example.playpal.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;
}
