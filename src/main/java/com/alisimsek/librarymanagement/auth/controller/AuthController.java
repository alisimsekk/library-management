package com.alisimsek.librarymanagement.auth.controller;

import com.alisimsek.librarymanagement.auth.controller.dto.request.LoginRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.request.RegisterRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.response.AuthResponse;
import com.alisimsek.librarymanagement.auth.service.AuthService;
import com.alisimsek.librarymanagement.common.response.ApiResponse;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Authenticate user", description = "Public endpoint - no authentication required")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.authenticateUser(loginRequest)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.registerUser(registerRequest)));
    }
}
