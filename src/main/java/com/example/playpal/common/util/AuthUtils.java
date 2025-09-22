package com.example.playpal.common.util;

import com.example.playpal.common.exception.UnauthorizedException;
import com.example.playpal.common.security.JwtAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    public Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthentication jwtAuth) || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return Long.parseLong(jwtAuth.getPrincipal().toString());
    }
}
