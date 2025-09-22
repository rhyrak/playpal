package com.example.playpal.auth.filter;

import com.example.playpal.auth.config.TokenConfiguration;
import com.example.playpal.auth.service.TokenService;
import com.example.playpal.common.security.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final TokenConfiguration tokenConfiguration;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(tokenConfiguration.getTokenHeaderPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authHeader.replace(tokenConfiguration.getTokenHeaderPrefix(), "");
        var claims = tokenService.validateAndExtractClaims(jwt);

        JwtAuthentication authentication = new JwtAuthentication(claims);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
