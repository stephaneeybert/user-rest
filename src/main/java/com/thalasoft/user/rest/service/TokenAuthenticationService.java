package com.thalasoft.user.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TokenAuthenticationService {

	public void addTokenToResponseHeader(HttpHeaders headers, String username);

	public void addTokenToResponseHeader(HttpServletResponse response, Authentication authentication);
	
	public Authentication authenticate(HttpServletRequest request);

	public Authentication authenticateFromRefreshToken(HttpServletRequest request);
	
}
