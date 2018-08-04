package com.thalasoft.user.rest.security;

import java.util.ArrayList;
import java.util.List;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.rest.service.CredentialsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    CredentialsService credentialsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return credentialsService.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // boolean value = authentication.equals(UsernamePasswordAuthenticationToken.class);
        boolean value = (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
        return value;
    }

    // @Bean // TODO this is deprecated - use oauth2
    // public static NoOpPasswordEncoder passwordEncoder() {
    //     return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    // }

}