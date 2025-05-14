package com.alisimsek.librarymanagement.auth.service;

import com.alisimsek.librarymanagement.auth.controller.dto.request.LoginRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.request.RegisterRequest;
import com.alisimsek.librarymanagement.auth.controller.dto.response.AuthResponse;
import com.alisimsek.librarymanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.librarymanagement.security.CustomUserDetailsService;
import com.alisimsek.librarymanagement.security.JwtUtil;
import com.alisimsek.librarymanagement.user.controller.dto.converter.UserConverter;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import com.alisimsek.librarymanagement.user.entity.User;
import com.alisimsek.librarymanagement.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.alisimsek.librarymanagement.auth.AuthTestProvider.createAuthResponse;
import static com.alisimsek.librarymanagement.auth.AuthTestProvider.createLoginRequest;
import static com.alisimsek.librarymanagement.auth.AuthTestProvider.createRegisterRequest;
import static com.alisimsek.librarymanagement.user.UserTestProvider.createUserDto;
import static com.alisimsek.librarymanagement.user.UserTestProvider.createUserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private UserDetails userDetails;
    private AuthResponse authResponse;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        loginRequest = createLoginRequest();
        registerRequest = createRegisterRequest();
        userDetails = mock(UserDetails.class);
        authResponse = createAuthResponse();
        user = createUserEntity();
        userDto = createUserDto();
    }

    @Test
    void authenticateUser_ShouldReturnAuthResponse() {
        // Given
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
        when(jwtUtil.getAuthResponse(userDetails)).thenReturn(authResponse);

        // When
        AuthResponse result = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals(authResponse.getAccessToken(), result.getAccessToken());
        assertEquals(authResponse.getTokenType(), result.getTokenType());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(loginRequest.getUsername());
        verify(jwtUtil).getAuthResponse(userDetails);
    }

    @Test
    void registerUser_ShouldReturnUserDto() {
        // Given
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userConverter.convert(user)).thenReturn(userDto);

        // When
        UserDto result = authService.registerUser(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        verify(userRepository).findByUsername(registerRequest.getUsername());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userConverter).convert(user);
    }

    @Test
    void registerUser_WhenUserExists_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(EntityAlreadyExistsException.class, () -> authService.registerUser(registerRequest));
        verify(userRepository).findByUsername(registerRequest.getUsername());
    }
} 