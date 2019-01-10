package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.security.AuthoritiesConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserFixtureService {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder userPasswordEncoder;

  public static final String USER_EMAIL = "mittiprovence@yahoo.se";
  public static final String USER_PASSWORD = "mignet";

  private User user0;

  public void addAuthenticatedUser() {
    user0 = new User();
    user0.setFirstname("Stephane");
    user0.setLastname("Eybert");
    user0.setEmail(new EmailAddress(USER_EMAIL));
    user0.setPassword(userPasswordEncoder.encode(USER_PASSWORD));
    try {
      user0 = userService.findByEmail(user0.getEmail().toString());
    } catch (EntityNotFoundException e) {
      user0 = userService.add(user0);
    }
    user0.addRole(AuthoritiesConstants.ROLE_ADMIN.getRole());
  }

  public void removeAuthenticatedUser() {
    userService.delete(user0.getId());
  }

}
