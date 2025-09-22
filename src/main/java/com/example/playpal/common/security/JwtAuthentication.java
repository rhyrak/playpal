package com.example.playpal.common.security;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthentication implements Authentication {
    private final Claims claims;
    @Getter
    private final UserType userType;
    private final List<GrantedAuthority> authorities;
    private boolean authenticated = true;
    private static final String ROLE_PREFIX = "ROLE_";

    public JwtAuthentication(Claims claims) {
        this.claims = claims;
        userType = UserType.valueOf(claims.get(JwtClaims.USER_TYPE.getValue()).toString());
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + userType.name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public Object getPrincipal() {
        return claims.get(JwtClaims.USER_ID.getValue());
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return claims.get(JwtClaims.USERNAME.getValue()).toString();
    }
}
