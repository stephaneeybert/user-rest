package com.thalasoft.user.rest.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// The default redirection on a failed authentication does not make sense for a REST request
// Instead of returning a: 301 MOVED PERMANENTLY simply return a: 401 UNAUTHORIZED
@Component
public final class RESTAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. You failed to authenticate.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("User REST");
        super.afterPropertiesSet();
    }

}