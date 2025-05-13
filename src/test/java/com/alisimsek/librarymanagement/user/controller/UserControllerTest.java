package com.alisimsek.librarymanagement.user.controller;

import com.alisimsek.librarymanagement.security.WithMockCustomUser;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import com.alisimsek.librarymanagement.user.entity.UserType;
import com.alisimsek.librarymanagement.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.alisimsek.librarymanagement.user.UserTestProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String userGuid;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        userGuid = UUID.randomUUID().toString();
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserCreateRequest request = createUserRequest();
        UserDto response = createUserDto();

        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value(response.getUsername()))
                .andExpect(jsonPath("$.data.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(response.getLastName()));

        verify(userService).createUser(any(UserCreateRequest.class));
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void getUserByGuid_ShouldReturnUserDetails() throws Exception {
        // Given
        UserDto response = createUserDto();
        when(userService.getUserByGuid(userGuid)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{guid}", userGuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value(response.getUsername()))
                .andExpect(jsonPath("$.data.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(response.getLastName()));

        verify(userService).getUserByGuid(userGuid);
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Given
        List<UserDto> userList = createUserDtoList();
        Page<UserDto> userPage = new PageImpl<>(userList);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(userList.size()))
                .andExpect(jsonPath("$.data[0].username").value(userList.get(0).getUsername()));

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void searchUsers_ShouldReturnFilteredUserList() throws Exception {
        // Given
        UserSearchRequest request = searchUserRequest();
        List<UserDto> userList = createUserDtoList();
        Page<UserDto> userPage = new PageImpl<>(userList);

        when(userService.searchUsers(any(UserSearchRequest.class), any(Pageable.class))).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/search")
                        .param("firstName", request.getFirstName())
                        .param("userType", request.getUserType().toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(userList.size()))
                .andExpect(jsonPath("$.data[0].username").value(userList.get(0).getUsername()));

        verify(userService).searchUsers(any(UserSearchRequest.class), any(Pageable.class));
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserUpdateRequest request = updateUserRequest();
        UserDto response = createUpdatedUserDto();

        when(userService.updateUser(eq(userGuid), any(UserUpdateRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/v1/users/{guid}", userGuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(response.getLastName()));

        verify(userService).updateUser(eq(userGuid), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void deleteUser_ShouldReturnSuccess() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(userGuid);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{guid}", userGuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(userService).deleteUser(userGuid);
    }

    @Test
    @WithMockCustomUser(userType = UserType.ADMIN)
    void activateUser_ShouldReturnActivatedUser() throws Exception {
        // Given
        UserDto response = createUserDto();
        when(userService.activateUser(userGuid)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/users/activate/{guid}", userGuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value(response.getUsername()))
                .andExpect(jsonPath("$.data.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(response.getLastName()));

        verify(userService).activateUser(userGuid);
    }
} 