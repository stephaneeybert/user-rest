package com.thalasoft.user.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thalasoft.user.rest.service.CredentialsService;
import com.thalasoft.user.rest.service.TokenAuthenticationService;
import com.thalasoft.user.rest.service.resource.CredentialsModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AuthenticationFromCredentialsFilter extends AbstractAuthenticationProcessingFilter {

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @Autowired
  CredentialsService credentialsService;

  public AuthenticationFromCredentialsFilter(final RequestMatcher requestMatcher) {
    super(requestMatcher);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {
      CredentialsModel credentialsResource = new ObjectMapper().readValue(req.getInputStream(), CredentialsModel.class);
      return credentialsService.authenticate(credentialsResource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authentication) throws IOException, ServletException {
    tokenAuthenticationService.addAccessTokenToResponseHeader(response, authentication);
    tokenAuthenticationService.addRefreshTokenToResponseHeader(request, response, authentication);
  }

}
