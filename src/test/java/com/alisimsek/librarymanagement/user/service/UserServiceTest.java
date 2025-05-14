package com.alisimsek.librarymanagement.user.service;

import com.alisimsek.librarymanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.librarymanagement.user.controller.dto.converter.UserConverter;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import com.alisimsek.librarymanagement.user.entity.User;
import com.alisimsek.librarymanagement.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.alisimsek.librarymanagement.user.UserTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserSearchRequest searchRequest;
    private User user;
    private User updatedUser;
    private User adminUser;
    private UserDto userDto;
    private UserDto updatedUserDto;
    private String userGuid;
    private List<User> userList;
    private Page<User> userPage;
    private BooleanBuilder booleanBuilder;

    @BeforeEach
    void setUp() {
        createRequest = createUserRequest();
        updateRequest = updateUserRequest();
        searchRequest = searchUserRequest();
        user = createUserEntity();
        updatedUser = createUpdatedUserEntity();
        adminUser = createAdminUserEntity();
        userDto = createUserDto();
        updatedUserDto = createUpdatedUserDto();
        userGuid = user.getGuid();
        userList = List.of(user, updatedUser);
        userPage = new PageImpl<>(userList);
        booleanBuilder = new BooleanBuilder();
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        // Given
        when(userRepository.findByUsername(createRequest.getUsername())).thenReturn(Optional.empty());
        when(userConverter.convertToEntity(createRequest)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userConverter.convert(user)).thenReturn(userDto);

        // When
        UserDto result = userService.createUser(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        verify(userRepository).findByUsername(createRequest.getUsername());
        verify(userConverter).convertToEntity(createRequest);
        verify(passwordEncoder).encode(createRequest.getPassword());
        verify(userRepository).save(user);
        verify(userConverter).convert(user);
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername(createRequest.getUsername())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(EntityAlreadyExistsException.class, () -> userService.createUser(createRequest));
        verify(userRepository).findByUsername(createRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByGuid_ShouldReturnUserDto() {
        // Given
        when(userRepository.getByGuid(userGuid)).thenReturn(user);
        when(userConverter.convert(user)).thenReturn(userDto);

        // When
        UserDto result = userService.getUserByGuid(userGuid);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        verify(userRepository).getByGuid(userGuid);
        verify(userConverter).convert(user);
    }

    @Test
    void getAllUsers_ShouldReturnUserDtoPage() {
        // Given
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userConverter.convert(any(User.class))).thenReturn(userDto).thenReturn(updatedUserDto);

        // When
        Page<UserDto> result = userService.getAllUsers(mock(Pageable.class));

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(userDto.getUsername(), result.getContent().get(0).getUsername());
        assertEquals(updatedUserDto.getUsername(), result.getContent().get(1).getUsername());
        verify(userRepository).findAll(any(Pageable.class));
        verify(userConverter, times(2)).convert(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto() {
        // Given
        when(userRepository.getByGuid(userGuid)).thenReturn(user);
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userConverter.convert(updatedUser)).thenReturn(updatedUserDto);

        // When
        UserDto result = userService.updateUser(userGuid, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedUserDto.getFirstName(), result.getFirstName());
        assertEquals(updatedUserDto.getLastName(), result.getLastName());
        verify(userRepository).getByGuid(userGuid);
        verify(passwordEncoder).encode(updateRequest.getPassword());
        verify(userRepository).save(user);
        verify(userConverter).convert(updatedUser);
    }

    @Test
    void updateUser_WithoutPassword_ShouldNotEncodePassword() {
        // Given
        updateRequest.setPassword(null);
        when(userRepository.getByGuid(userGuid)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userConverter.convert(updatedUser)).thenReturn(updatedUserDto);

        // When
        UserDto result = userService.updateUser(userGuid, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedUserDto.getFirstName(), result.getFirstName());
        assertEquals(updatedUserDto.getLastName(), result.getLastName());
        verify(userRepository).getByGuid(userGuid);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);
        verify(userConverter).convert(updatedUser);
    }

    @Test
    void deleteUser_ShouldDisableAndDeleteUser() {
        // Given
        when(userRepository.getByGuid(userGuid)).thenReturn(user);

        // When
        userService.deleteUser(userGuid);

        // Then
        verify(userRepository).getByGuid(userGuid);
        verify(userRepository).delete(user);
    }

    @Test
    void activateUser_ShouldEnableAndActivateUser() {
        // Given
        when(userRepository.getByGuid(userGuid)).thenReturn(user);
        when(userConverter.convert(user)).thenReturn(userDto);

        // When
        UserDto result = userService.activateUser(userGuid);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        verify(userRepository).getByGuid(userGuid);
        verify(userRepository).activate(user);
        verify(userConverter).convert(user);
    }
} 