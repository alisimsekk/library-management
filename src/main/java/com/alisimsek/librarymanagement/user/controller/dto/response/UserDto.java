package com.alisimsek.librarymanagement.user.controller.dto.response;

import com.alisimsek.librarymanagement.common.base.EntityStatus;
import com.alisimsek.librarymanagement.user.entity.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String guid;
    private String username;
    private String firstName;
    private String lastName;
    private UserType userType;
    private EntityStatus entityStatus;
}
