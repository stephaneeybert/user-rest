package com.thalasoft.user.rest.security;

import java.util.ArrayList;
import java.util.List;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.service.CredentialsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        User user = null;
        try {
        	user = credentialsService.findByEmail(new EmailAddress(email));
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("The login " + email + " and password could not match.");            	
        }
        if (user != null) {
            if (credentialsService.checkPassword(user, password)) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                return new UsernamePasswordAuthenticationToken(email, password, grantedAuthorities);
            } else {
                throw new BadCredentialsException("The login " + user.getEmail() + " and password could not match.");            	
            }
        }
        throw new BadCredentialsException("The login " + authentication.getPrincipal() + " and password could not match.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Bean // TODO this is deprecated - use oauth2
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

}