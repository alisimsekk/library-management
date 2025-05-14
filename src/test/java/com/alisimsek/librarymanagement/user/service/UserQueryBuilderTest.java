package com.alisimsek.librarymanagement.user.service;

import com.alisimsek.librarymanagement.common.base.EntityStatus;
import com.alisimsek.librarymanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.librarymanagement.user.entity.QUser;
import com.alisimsek.librarymanagement.user.entity.UserType;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserQueryBuilderTest {

    @Test
    void createQuery_WhenAllFieldsProvided_ShouldCreateCompleteQuery() {
        // Given
        UserSearchRequest request = new UserSearchRequest();
        request.setUsername("testuser");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setUserType(UserType.PATRON);
        request.setEntityStatus(EntityStatus.ACTIVE);

        // When
        BooleanBuilder result = UserQueryBuilder.createQuery(request);

        // Then
        assertNotNull(result);
        String queryString = result.toString();
        assertTrue(queryString.contains(QUser.user.username.containsIgnoreCase("testuser").toString()));
        assertTrue(queryString.contains(QUser.user.firstName.containsIgnoreCase("Test").toString()));
        assertTrue(queryString.contains(QUser.user.lastName.containsIgnoreCase("User").toString()));
        assertTrue(queryString.contains(QUser.user.userType.eq(UserType.PATRON).toString()));
        assertTrue(queryString.contains(QUser.user.entityStatus.eq(EntityStatus.ACTIVE).toString()));
    }

    @Test
    void createQuery_WhenOnlyEntityStatusProvided_ShouldCreateQueryWithEntityStatus() {
        // Given
        UserSearchRequest request = new UserSearchRequest();
        request.setEntityStatus(EntityStatus.DELETED);

        // When
        BooleanBuilder result = UserQueryBuilder.createQuery(request);

        // Then
        assertNotNull(result);
        String queryString = result.toString();
        assertTrue(queryString.contains(QUser.user.entityStatus.eq(EntityStatus.DELETED).toString()));
        assertFalse(queryString.contains(QUser.user.username.toString()));
        assertFalse(queryString.contains(QUser.user.firstName.toString()));
        assertFalse(queryString.contains(QUser.user.lastName.toString()));
        assertFalse(queryString.contains(QUser.user.userType.toString()));
    }
} 