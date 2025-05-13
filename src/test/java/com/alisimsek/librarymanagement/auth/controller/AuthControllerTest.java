package com.alisimsek.librarymanagement.auth.controller;

import com.alisimsek.librarymanagement.auth.controller.dto.request.LoginRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.request.RegisterRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.response.AuthResponse;
import com.alisimsek.librarymanagement.auth.service.AuthService;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.alisimsek.librarymanagement.auth.AuthTestProvider.*;
import static com.alisimsek.librarymanagement.user.UserTestProvider.createUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization
    }

    @Test
    void authenticateUser_ShouldReturnAuthResponse() throws Exception {
        // Given
        LoginRequest request = createLoginRequest();
        AuthResponse response = createAuthResponse();

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value(response.getAccessToken()))
                .andExpect(jsonPath("$.data.tokenType").value(response.getTokenType()));

        verify(authService).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void registerUser_ShouldReturnUserDto() throws Exception {
        // Given
        RegisterRequest request = createRegisterRequest();
        UserDto response = createUserDto();

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value(response.getUsername()))
                .andExpect(jsonPath("$.data.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(response.getLastName()));

        verify(authService).registerUser(any(RegisterRequest.class));
    }
    
    @Test
    void authenticateUser_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void registerUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("not-an-email")
                .password("weak")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(any(RegisterRequest.class));
    }
    
/*    @Test
    void authenticateUser_WithServiceException_ShouldReturnError() throws Exception {
        // Given
        LoginRequest request = createLoginRequest();
        
        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(authService).authenticateUser(any(LoginRequest.class));
    }*/
} 