package com.thalasoft.user.rest.controller;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.assembler.UserModelAssembler;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.service.CredentialsService;
import com.thalasoft.user.rest.service.TokenAuthenticationService;
import com.thalasoft.user.rest.service.resource.CredentialsModel;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + DomainConstants.AUTH)
public class AuthorizationController {

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @Autowired
  private UserService userService;

  @Autowired
  private CredentialsService credentialsService;

  @Autowired
  private UserModelAssembler userModelAssembler;

  // TODO This method is not needed since there is already a filter doing the job
  @PostMapping(value = RESTConstants.SLASH + DomainConstants.LOGIN)
  @ResponseBody
  public ResponseEntity<UserModel> login(@Valid @RequestBody CredentialsModel credentialsModel,
      UriComponentsBuilder builder) {
    HttpHeaders responseHeaders = new HttpHeaders();
    try {
      User user = credentialsService.checkPassword(credentialsModel);
      userService.clearReadablePassword(user);
      tokenAuthenticationService.addAccessTokenToResponseHeader(responseHeaders, credentialsModel.getEmail());
      URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
          .buildAndExpand(user.getId()).toUri();
      UserModel createdUserModel = userModelAssembler.toModel(user);
      return ResponseEntity.created(location).headers(responseHeaders).body(createdUserModel);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping(value = RESTConstants.SLASH + DomainConstants.TOKEN_REFRESH)
  @ResponseBody
  public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder builder) throws IOException, ServletException {
    Authentication authentication = tokenAuthenticationService.authenticateFromRefreshToken(request);
    // Only the access token is refreshed
    // Refresing the refresh token would be like giving a never expiring refresh
    // token
    tokenAuthenticationService.addAccessTokenToResponseHeader(response, authentication);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.TOKEN_REFRESH).buildAndExpand().toUri();
    return ResponseEntity.created(location).build();
  }

  @PostMapping(value = RESTConstants.SLASH + DomainConstants.LOGOUT)
  @ResponseBody
  public ResponseEntity<Void> logout(UriComponentsBuilder builder) {
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.LOGOUT).buildAndExpand().toUri();
    return ResponseEntity.ok().location(location).build();
  }

}
