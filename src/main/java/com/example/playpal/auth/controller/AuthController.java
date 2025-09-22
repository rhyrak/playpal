package com.example.playpal.auth.controller;

import com.example.playpal.auth.model.dto.LoginRequest;
import com.example.playpal.auth.model.dto.RegisterRequest;
import com.example.playpal.auth.model.dto.TokenRefreshRequest;
import com.example.playpal.auth.model.dto.TokenResponse;
import com.example.playpal.auth.model.mapper.TokenPairToTokenResponseMapper;
import com.example.playpal.auth.service.LoginService;
import com.example.playpal.auth.service.LogoutService;
import com.example.playpal.auth.service.RefreshTokenService;
import com.example.playpal.auth.service.RegisterService;
import com.example.playpal.common.docs.UnauthorizedApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Bearer",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER)
public class AuthController {
    private final LoginService loginService;
    private final RegisterService registerService;
    private final LogoutService logoutService;
    private final RefreshTokenService refreshTokenService;


    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with type USER"
    )
    @ApiResponse(responseCode = "200", description = "User successfully registered")
    @ApiResponse(responseCode = "400", description = "Invalid register details provided")
    @ApiResponse(responseCode = "409", description = "A user with the same email or username already exists")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var tokenPair = registerService.registerUser(registerRequest);
        var response = TokenPairToTokenResponseMapper.INSTANCE.map(tokenPair);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Login user",
            description = "Generates access and refresh tokens"
    )
    @ApiResponse(responseCode = "200", description = "User successfully logged in")
    @ApiResponse(responseCode = "400", description = "Invalid login details provided")
    @ApiResponse(responseCode = "401", description = "User not found or invalid email & password combination.")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var tokenPair = loginService.login(loginRequest);
        var response = TokenPairToTokenResponseMapper.INSTANCE.map(tokenPair);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Logout user",
            description = "Logs out the user from all devices."
    )
    @ApiResponse(responseCode = "200", description = "User successfully logged out")
    @UnauthorizedApiResponse
    @SecurityRequirement(name = "Bearer")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        logoutService.logout();
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Refresh tokens",
            description = "Generates new access and refresh tokens"
    )
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token provided.")
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        var tokenPair = refreshTokenService.refreshToken(tokenRefreshRequest);
        var response = TokenPairToTokenResponseMapper.INSTANCE.map(tokenPair);
        return ResponseEntity.ok(response);
    }
}
