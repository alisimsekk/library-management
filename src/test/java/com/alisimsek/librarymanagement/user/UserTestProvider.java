package com.alisimsek.librarymanagement.user;

import com.alisimsek.librarymanagement.common.base.EntityStatus;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import com.alisimsek.librarymanagement.user.entity.User;
import com.alisimsek.librarymanagement.user.entity.UserType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserTestProvider {

    public static UserCreateRequest createUserRequest() {
        return UserCreateRequest.builder()
                .username("test@example.com")
                .password("Test1234")
                .firstName("Test")
                .lastName("User")
                .userType(UserType.PATRON)
                .build();
    }

    public static UserUpdateRequest updateUserRequest() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setPassword("Updated1234");
        return request;
    }

    public static UserSearchRequest searchUserRequest() {
        UserSearchRequest request = new UserSearchRequest();
        request.setFirstName("Test");
        request.setUserType(UserType.PATRON);
        return request;
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .guid(UUID.randomUUID().toString())
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .userType(UserType.PATRON)
                .entityStatus(EntityStatus.ACTIVE)
                .build();
    }

    public static UserDto createUpdatedUserDto() {
        return UserDto.builder()
                .guid(UUID.randomUUID().toString())
                .username("test@example.com")
                .firstName("Updated")
                .lastName("User")
                .userType(UserType.LIBRARIAN)
                .entityStatus(EntityStatus.ACTIVE)
                .build();
    }

    public static List<UserDto> createUserDtoList() {
        return Arrays.asList(
                createUserDto(),
                UserDto.builder()
                        .guid(UUID.randomUUID().toString())
                        .username("another@example.com")
                        .firstName("Another")
                        .lastName("User")
                        .userType(UserType.LIBRARIAN)
                        .entityStatus(EntityStatus.ACTIVE)
                        .build()
        );
    }

    public static User createUserEntity() {
        User user = new User();
        user.setGuid(UUID.randomUUID().toString());
        user.setUsername("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserType(UserType.PATRON);
        user.setEntityStatus(EntityStatus.ACTIVE);
        return user;
    }

    public static User createUpdatedUserEntity() {
        User user = new User();
        user.setGuid(UUID.randomUUID().toString());
        user.setUsername("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Updated");
        user.setLastName("User");
        user.setUserType(UserType.LIBRARIAN);
        user.setEntityStatus(EntityStatus.ACTIVE);
        return user;
    }

    public static User createAdminUserEntity() {
        User user = new User();
        user.setGuid(UUID.randomUUID().toString());
        user.setUsername("admin@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setUserType(UserType.ADMIN);
        user.setEntityStatus(EntityStatus.ACTIVE);
        return user;
    }
} 