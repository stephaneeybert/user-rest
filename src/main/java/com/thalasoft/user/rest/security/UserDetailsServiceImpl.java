package com.thalasoft.user.rest.security;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.service.CredentialsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private CredentialsService credentialsService;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username != null && !username.isEmpty()) {
      try {
        User user = credentialsService.findByEmail(new EmailAddress(username));
        return new UserDetailsWrapper(user);
      } catch (EntityNotFoundException e) {
        throw new UsernameNotFoundException("The user " + username + " was not found.");
      }
    }
    throw new UsernameNotFoundException("The user " + username + " was not found.");
  }

}
