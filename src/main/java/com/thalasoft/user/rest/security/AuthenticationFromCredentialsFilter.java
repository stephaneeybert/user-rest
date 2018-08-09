package com.thalasoft.user.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thalasoft.user.rest.service.CredentialsService;
import com.thalasoft.user.rest.service.TokenAuthenticationService;
import com.thalasoft.user.rest.service.resource.CredentialsResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

public class AuthenticationFromCredentialsFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	CredentialsService credentialsService;

	public AuthenticationFromCredentialsFilter() {
		super(new AntPathRequestMatcher("/users/login", RequestMethod.POST.name()));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			CredentialsResource credentialsResource = new ObjectMapper().readValue(req.getInputStream(),
					CredentialsResource.class);
			return credentialsService.authenticate(credentialsResource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		tokenAuthenticationService.addTokenToResponseHeader(response, authentication);
	}

}
