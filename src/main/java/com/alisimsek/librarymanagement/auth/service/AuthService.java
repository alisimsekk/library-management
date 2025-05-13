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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.alisimsek.librarymanagement.user.entity.User.createUser;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;


    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
        return jwtUtil.getAuthResponse(userDetails);
    }

    public UserDto registerUser(RegisterRequest registerRequest) {
        Optional<User> userFromDb = userRepository.findByUsername(registerRequest.getUsername());
        if (userFromDb.isPresent()) {
            throw new EntityAlreadyExistsException();
        }
        User newUser = createUser(registerRequest, registerRequest.getUserType());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser = userRepository.save(newUser);
        return userConverter.convert(newUser);
    }
}
