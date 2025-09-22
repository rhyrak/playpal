package com.example.playpal.integration.auth;

import com.example.playpal.auth.model.dto.LoginRequest;
import com.example.playpal.auth.model.dto.RegisterRequest;
import com.example.playpal.auth.model.dto.TokenRefreshRequest;
import com.example.playpal.auth.model.dto.TokenResponse;
import com.example.playpal.common.model.ErrorResponse;
import com.example.playpal.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest extends AbstractIntegrationTest {
    public static final String AUTH_API_ENDPOINT = "/api/v1/auth";

    @Autowired
    TestRestTemplate restTemplate;

    private static TokenResponse loginTokenResponse;

    @Test
    @Order(1)
    void givenInvalidRegisterRequest_whenRegisterUser_thenReturnBadRequest() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("")
                .password("")
                .email("")
                .build();

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/register", registerRequest, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(3, res.getBody().getErrors().size());
        assertFalse(res.getBody().getMessage().isEmpty());
    }

    @Test
    @Order(2)
    void givenValidRegisterRequest_whenRegisterUser_thenReturnTokenPair() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("username")
                .password("password")
                .email("username@example.com")
                .build();

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/register", registerRequest, TokenResponse.class);

        // Then
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertNotNull(res.getBody().getAccessToken());
        assertNotNull(res.getBody().getRefreshToken());
    }

    @ParameterizedTest
    @CsvSource({
            "username,username@example.com",
            "username,newmail@example.com",
            "newusername,username@example.com"
    })
    @Order(3)
    void givenExistingUsernameOrEmail_whenRegisterUser_thenReturnConflict(String username, String email) {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(email)
                .password("password")
                .build();

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/register", registerRequest, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    @Order(4)
    void givenValidLoginRequest_whenLoginUser_thenReturnTokenPair() {
        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("username@example.com")
                .password("password")
                .build();

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/login", loginRequest, TokenResponse.class);
        loginTokenResponse = res.getBody();

        // Then
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertNotNull(res.getBody().getAccessToken());
        assertNotNull(res.getBody().getRefreshToken());
    }

    @ParameterizedTest
    @CsvSource({
            "username@example.com,wrongpassword",
            "not_registered@example.com,any_password"
    })
    @Order(5)
    void givenIncorrectLoginRequest_whenLoginUser_thenReturnUnauthorized(String email, String password) {
        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/login", loginRequest, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertNotNull(res.getBody());
        assertFalse(res.getBody().getMessage().isEmpty());
    }

    @Test
    @Order(6)
    void givenInvalidLoginRequest_whenLoginUser_thenReturnBadRequest() {
        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("not_a_mail")
                .password("")
                .build();

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/login", loginRequest, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(2, res.getBody().getErrors().size());
        assertFalse(res.getBody().getMessage().isEmpty());

        System.out.println(res.getBody());
    }

    @Test
    @Order(7)
    void givenValidTokenRefreshRequest_whenRefreshToken_thenReturnTokenResponse() {
        // Given
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(loginTokenResponse.getRefreshToken());

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/refresh", refreshRequest, TokenResponse.class);

        // Then
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertNotNull(res.getBody().getAccessToken());
        assertNotNull(res.getBody().getRefreshToken());
    }

    @Test
    @Order(8)
    void givenInvalidAuthenticationHeader_whenLogout_thenUnauthorized() {
        // Given
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Nope " + loginTokenResponse.getAccessToken());

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/logout", new HttpEntity<>(httpHeaders), Object.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    @Order(9)
    void givenInvalidAccessToken_whenLogout_thenUnauthorized() {
        // Given
        var badToken = loginTokenResponse.getAccessToken().substring(0, loginTokenResponse.getAccessToken().length() - 1);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + badToken);

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/logout", new HttpEntity<>(httpHeaders), Object.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    @Order(10)
    void givenValidAccessToken_whenLogout_thenOk() {
        // Given
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + loginTokenResponse.getAccessToken());

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/logout", new HttpEntity<>(httpHeaders), Object.class);

        // Then
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(11)
    void givenValidTokenRefreshRequestAfterLogout_whenRefreshToken_thenUnauthorized() {
        // Given
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(loginTokenResponse.getRefreshToken());

        // When
        var res = restTemplate.postForEntity(AUTH_API_ENDPOINT + "/refresh", refreshRequest, Object.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }
}
