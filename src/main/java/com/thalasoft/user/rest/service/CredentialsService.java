package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.service.resource.CredentialsModel;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface CredentialsService {

  public User findByEmail(EmailAddress email);

  public boolean checkPassword(User user, String password);

  public User checkPassword(CredentialsModel credentialsModel);

  public User updatePassword(Long id, String password);

  public Authentication authenticate(Authentication authentication) throws AuthenticationException;

  public Authentication authenticate(CredentialsModel credentialsModel) throws AuthenticationException;

}
