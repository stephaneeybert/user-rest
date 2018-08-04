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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFromCredentialsFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	CredentialsService credentialsService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			CredentialsResource credentialsResource = new ObjectMapper().readValue(req.getInputStream(),
					CredentialsResource.class);
			return authenticationManager.authenticate(credentialsService.authenticate(credentialsResource));
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
