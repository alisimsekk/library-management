package com.alisimsek.librarymanagement.security;

import com.alisimsek.librarymanagement.user.entity.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(customUser.userType().name());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUser.username(), 
                "password", 
                Collections.singletonList(authority));
        
        context.setAuthentication(auth);
        return context;
    }
} 