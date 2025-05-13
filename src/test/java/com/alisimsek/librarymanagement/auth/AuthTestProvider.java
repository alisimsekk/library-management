package com.alisimsek.librarymanagement.auth;

import com.alisimsek.librarymanagement.auth.controller.dto.request.LoginRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.request.RegisterRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.response.AuthResponse;
import com.alisimsek.librarymanagement.user.entity.UserType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthTestProvider {

    public static LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("Test1234");
        return request;
    }

    public static RegisterRequest createRegisterRequest() {
        return RegisterRequest.builder()
                .username("new@example.com")
                .password("Test1234")
                .firstName("New")
                .lastName("User")
                .userType(UserType.PATRON)
                .build();
    }

    public static AuthResponse createAuthResponse() {
        return AuthResponse.builder()
                .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .tokenType("Bearer")
                .expirationDate(LocalDateTime.now().plusHours(1))
                .build();
    }
} 