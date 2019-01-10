package com.thalasoft.user.rest.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thalasoft.user.rest.security.service.TokenAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AuthenticationFromTokenFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	public AuthenticationFromTokenFilter(final RequestMatcher requestMatcher) {
		super(requestMatcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		return tokenAuthenticationService.authenticate(request);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			FilterChain filterChain, Authentication authentication) throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(httpRequest, httpResponse);
	}

}
