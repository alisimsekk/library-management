package com.alisimsek.librarymanagement.security;

import com.alisimsek.librarymanagement.user.entity.UserType;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "test@example.com";
    UserType userType() default UserType.PATRON;
} 