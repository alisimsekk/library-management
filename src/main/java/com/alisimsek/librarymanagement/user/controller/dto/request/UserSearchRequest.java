package com.alisimsek.librarymanagement.user.controller.dto.request;

import com.alisimsek.librarymanagement.common.base.EntityStatus;
import com.alisimsek.librarymanagement.user.entity.UserType;
import lombok.Data;

@Data
public class UserSearchRequest {
    private String username;
    private String firstName;
    private String lastName;
    private UserType userType;
    private EntityStatus entityStatus = EntityStatus.ACTIVE;
}
