package com.thalasoft.user.rest.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;

@Transactional
public interface TokenAuthenticationService {

  public void addAccessTokenToResponseHeader(HttpHeaders headers, String username);

  public void addRefreshTokenToResponseHeader(HttpHeaders headers, String username, String clientId);

  public void addAccessTokenToResponseHeader(HttpServletResponse response, Authentication authentication);

  public void addRefreshTokenToResponseHeader(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication);

  public Authentication authenticate(HttpServletRequest request);

  public Authentication authenticateFromRefreshToken(HttpServletRequest request);

  public Date getIssuedAtDate();

  public Date getExpirationDate();

  public Claims addClaimstoToken(UserDetails userDetails);

}
