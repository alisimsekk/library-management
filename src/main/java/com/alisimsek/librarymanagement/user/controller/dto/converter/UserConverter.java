package com.alisimsek.librarymanagement.user.controller.dto.converter;

import com.alisimsek.librarymanagement.common.BaseConverter;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.librarymanagement.user.controller.dto.response.UserDto;
import com.alisimsek.librarymanagement.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter extends BaseConverter<User, UserDto> {

    @Override
    public UserDto convert(User source) {
        return UserDto.builder()
                .guid(source.getGuid())
                .entityStatus(source.getEntityStatus())
                .username(source.getUsername())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .userType(source.getUserType())
                .build();
    }

    public User convertToEntity(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserType(request.getUserType());
        return user;
    }
}
